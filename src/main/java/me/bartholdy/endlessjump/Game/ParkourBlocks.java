package me.bartholdy.endlessjump.Game;

import lombok.Getter;
import lombok.Setter;
import me.bartholdy.endlessjump.GameAPI.CoordinateUtil;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParkourBlocks {
    private static final ComponentLogger logger = ComponentLogger.logger(ParkourBlocks.class);
    private List<Pos> positionsliste = new ArrayList<>();     /* Current block sequence */
    private int iteration = 1;                          /*  How many block sequences were generated
                                                           i.e. how often does player reached "the checkpoint" */
    private final int SEQUENCE_SIZE = 10;               /* How many blocks should be generated */
    private boolean isBlocked;
    private final Pos startPosition;
    private final int teleportThreshold;
    private final ParkourPlayer player;
    private int deathScore = 0;
    private int teleportThresholdAdjusted;
    private ParkourSidebar sidebar;


    public ParkourBlocks(ParkourPlayer parkourPlayer) {
        Parkour parkour = Parkour.getInstance();
        this.player = parkourPlayer;
        this.startPosition = parkour.getInstances().getFirst().getWorldSpawnPosition();
        this.teleportThreshold = startPosition.blockY() - 3;
        this.teleportThresholdAdjusted = teleportThreshold;
        this.sidebar = new ParkourSidebar();
    }

    private void generateSequence(Player player, Pos recursivePos) {
        Pos pos;
        if (isFirstIteration()) {
            pos = player.getPosition().add(0, -1, 0);
            player.sendMessage(Component.text("FIRST ITERATION", NamedTextColor.RED));
//            player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 9999999));
            player.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.BLOCK, 1f, 1f));
            sidebar.show();
        }

        pos = recursivePos;
        sidebar.update();
        if (positionsliste.isEmpty()) {
            player.sendMessage("Calculating…");
            // Generate positions
            for (int i = 1; i < SEQUENCE_SIZE; i++) {
                pos = nextRandom(pos);
                positionsliste.add(pos);
            }
            // Set blocks
            Instance instance = player.getInstance();
            positionsliste.forEach(blockPos -> {
                instance.setBlock(blockPos, Block.GRASS_BLOCK);
                instance.setBlock(blockPos.add(0, -1, 0), Block.END_ROD);
            });

        } else {
            ParkourBlockThread parkourBlockThread = new ParkourBlockThread();
            parkourBlockThread.start(player);
            parkourBlockThread.getQueue().addAll(positionsliste);
            Pos lastPos = positionsliste.getLast();
            positionsliste.clear();
            player.sendMessage("Re-Calculating…");
            generateSequence(player, lastPos);

            float health = player.getHealth();
            if (health < 20) {
                player.setHealth(health + 1);
                player.playSound(Sound.sound(SoundEvent.ENTITY_SPLASH_POTION_THROW, Sound.Source.BLOCK, 1f, 1f));
            }
        }
    }

    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!isBlocked) {
            isBlocked = true;
            generateSequence(player);

        } else if (shouldRecalculate(player.getPosition())) {       // player#getPosition or event#getNewPosition ?
            isBlocked = false;
            iteration++;
            player.sendMessage(Component.text("Check point set", NamedTextColor.GREEN));

        } else if (shouldRecalculateSave(player.getPosition())) {    // player#getPosition or event#getNewPosition ?
            isBlocked = false;
            iteration++;
            player.sendMessage(Component.text("Checkpoint missed", NamedTextColor.RED));
        }

        // Teleport on fail
        if (player.getPosition().y() < teleportThresholdAdjusted) {
            Teleport();
            handleHealth();
        }
    }

    private void generateSequence(Player player) {
        generateSequence(player, getBlockPosUnderPlayer(player.getPosition()));
    }

    /**
     * Innerer Teil der rekursieven Erstellung der Positionssequenz (aus 10 Pos.)
     * <p>
     * Distanziert sich von ggb. Position
     * Bei Iterierung erhält man eine Positionssequenz
     */
    private Pos nextRandom(Pos previous) {
        Random random = new Random();
        int pX = previous.blockX();
        int pY = previous.blockY();
        int pZ = previous.blockZ();

        int rX = (pX + random.nextInt(4 - 1 + 1) + 1);
        int rZ = (pZ + random.nextInt(4 - 1 + 1) + 1);

        // Fix difficult jump
        if ((pX + 4 == rX) && (pZ + 4 == rZ)) {
            if (random.nextBoolean()) {
                rZ--;
            } else {
                rX--;
            }
            logger.info(Component.text("Fix difficult jump (4)"));
        }
        if ((pX + 5 == rX) && (pZ + 5 == rZ)) {
            if (random.nextBoolean()) {
                rZ--;
            } else {
                rX--;
            }
            logger.info(Component.text("Fix difficult jump (5)"));
        }

        // Fix "glued" blocks
        //  i.e. blocks that have no void between
        if ((pX + 1 == rX) && (pZ + 1 == rZ)) {
            rZ = rZ + 2;
            rX = rX + 2;
            logger.info(Component.text("Fix applied: relocate glued blocks (+) [x={" + rX + "} z={" + rZ + "}]"));
        }
        if ((pX - 1 == rX) && (pZ - 1 == rZ)) {
            rZ--;
            rX--;
            logger.info(Component.text("Fix applied: relocate glued blocks (-) [x={" + rX + "} z={" + rZ + "}]"));
        }
        return new Pos(rX, pY, rZ);
    }

    private boolean isFirstIteration() {
        return iteration == 1;
    }

    private Pos getBlockPosUnderPlayer(Pos pos) {
        return pos.add(0, -1, 0);
    }

    /**
     * Erster Checkoint 3 Blöcke vor Sequenzende.
     */
    private boolean shouldRecalculate(Pos playerPos) {
        return CoordinateUtil.comparePos(playerPos, positionsliste.get
                (SEQUENCE_SIZE - (3 + 1)) // 4 blocks before end
        );
    }

    /**
     * Zweiter Checkpoint
     * Sollte man den ersten Block nicht erwischt oder
     * übersprungen haben, hat man einen zweiten "Checkpoint".
     */
    private boolean shouldRecalculateSave(Pos playerPos) {
        return CoordinateUtil.comparePos(playerPos, positionsliste.get
                (SEQUENCE_SIZE - (2 + 1)) // 3 blocks before end
        );
    }

    private void Teleport() {
        if (iteration >= 2) {
            Pos blockPos = positionsliste.getFirst().add(0, 1, 0);
            blockPos = blockPos.withYaw(-45);
            player.teleport(blockPos);
            player.sendMessage(Component.text("Moved to start of ", NamedTextColor.GREEN)
                    .append(Component.text(iteration + ". ", NamedTextColor.YELLOW))
                    .append(Component.text("iteration", NamedTextColor.GREEN)));
        } else {
            Pos pos = Parkour.getInstance().getInstances().getFirst().getWorldSpawnPosition().add(0, 1, 0);
            pos = pos.withYaw(-45);
            player.teleport(pos);
        }
    }

    private void undo() {
        if (!positionsliste.isEmpty())
            for (Pos pos : positionsliste) {
                Parkour.getInstance().getInstances().getFirst().setBlock(pos, Block.AIR);
                Parkour.getInstance().getInstances().getFirst().setBlock(pos.add(0, -1, 0), Block.AIR);
            }
    }

    private void handleHealth() {
        float health = player.getHealth();
        if (health > 2) {
            player.setHealth(health - 2);
            player.playSound(Sound.sound(SoundEvent.ENTITY_CAT_HURT, Sound.Source.MASTER, 1f, 1f));

            switch ((int) health) {
                case 16 -> setTeleportThresholdAdjusted(teleportThresholdAdjusted - 5);
                case 10 -> setTeleportThresholdAdjusted(teleportThresholdAdjusted - 10);
                case 6 -> setTeleportThresholdAdjusted(teleportThresholdAdjusted - 25);
            }

        } else {
            // Player died
            deathScore++;
            setTeleportThresholdAdjusted(teleportThreshold); // Reset teleport height
            player.setEnableRespawnScreen(false);
            player.setHealth(20F);
            player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1f, 1f));
        }
    }

    private void setTeleportThresholdAdjusted(int threshold) {
        teleportThresholdAdjusted = threshold;
    }

    public void start() {
        isBlocked = true;
        generateSequence(player);
    }

    public void stop() {
        undo();
        iteration = 1;
        isBlocked = false;
        sidebar.remove();
        positionsliste.clear();
        deathScore = 0; // Beibelassen?
        player.setHealth(20F);
        player.clearEffects();
        player.setExp(0);
        player.teleport(player.getPosition().withPitch(90F));
        player.setPose(Entity.Pose.SWIMMING);
        player.setVelocity(new Vec(0, 999, 0));
        player.kill();
    }

    public void onSupport() {
        Joke joke = Joke.getRandomJoke();
        Component component = Component.text().appendNewline()
                .append(Component.text(joke.setup)).appendNewline()
                .append(Component.text(joke.punchline)).appendNewline()
                .build();
        player.sendMessage(component);
    }

    public void onReset() {
        Teleport();
        handleHealth();
    }

    public void onQuit() {
        stop();
    }

    class ParkourSidebar extends Sidebar {
        private final Component progressLineComponent = Component.text("Progress ", NamedTextColor.GRAY).append(Component.text(iteration, NamedTextColor.YELLOW));
        private final Component deathsLineComponent = Component.text("Deaths ", NamedTextColor.GRAY).append(Component.text(deathScore, NamedTextColor.YELLOW));
        private final Component scoreLineComponent = Component.text("Score ", NamedTextColor.GRAY).append(Component.text(iteration, NamedTextColor.YELLOW));

        public ParkourSidebar() {
            super(Component.text(" – JumpBoard – ", NamedTextColor.DARK_AQUA));
            createLine(new ScoreboardLine(
                    "progress",
                    progressLineComponent,
                    3
            ));
            createLine(new ScoreboardLine("empty", Component.empty(), 2));
            createLine(new ScoreboardLine(
                    "deaths",
                    deathsLineComponent,
                    1
            ));
            createLine(new ScoreboardLine(
                    "score",
                    scoreLineComponent,
                    0
            ));
        }

        public void update() {
            updateLineContent("progress", progressLineComponent);
            updateLineContent("deaths", progressLineComponent);
        }

        public void show() {
            addViewer(player);
        }

        public void remove() {
            if (isViewer(player))
                removeViewer(player);
        }
    }
}
