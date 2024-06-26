package me.bartholdy.endlessjump.Game;

import lombok.Getter;
import lombok.Setter;
import me.bartholdy.endlessjump.GameAPI.CoordinateUtil;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.scoreboard.Sidebar;
import net.minestom.server.sound.SoundEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParkourBlocks {
    private static final ComponentLogger logger = ComponentLogger.logger(ParkourBlocks.class);
    @Getter
    public List<Pos> blockList = new ArrayList<>();     /* Current block sequence */
    private int iteration = 1;                          /*  How many block sequences were generated
                                                           i.e. how often does player reached "the checkpoint" */
    private final int SEQUENCE_SIZE = 10;               /* How many blocks should be generated */
    private boolean isBlocked;
    @Getter
    private final Pos startPosition;
    @Getter
    private final int teleportThreshold;
    @Getter
    private final ParkourPlayer player;
    @Getter
    private Sidebar sidebar;
    private int deathScore = 0;
    @Setter
    private int teleportThresholdAdjusted;


    public ParkourBlocks(ParkourPlayer parkourPlayer) {
        Parkour parkour = Parkour.getInstance();
        this.startPosition = parkour.getInstances().getFirst().getWorldSpawnPosition();
        this.teleportThreshold = startPosition.blockY() - 3;
        this.teleportThresholdAdjusted = teleportThreshold;
        this.player = parkourPlayer;
    }

    public void generateSequence(Player player, Pos recursivePos) {
        Pos pos = (isFirstIteration() ? player.getPosition().add(0, -1, 0) : recursivePos);
        if (isFirstIteration()) {
            player.sendMessage(Component.text("FIRST ITERATION", NamedTextColor.RED));
//            player.addEffect(new Potion(PotionEffect.DARKNESS, (byte) 1, 9999999));
            player.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.BLOCK, 1f, 1f));
            setupScoreboard();
            updateScoreboard();
        }

        if (blockList.isEmpty()) {
            player.sendMessage("Calculating…");
            // Generate positions
            for (int i = 1; i < SEQUENCE_SIZE; i++) {
                pos = nextRandom(pos);
                blockList.add(pos);
            }
            // Set blocks
            Instance instance = player.getInstance();
            blockList.forEach(blockPos -> {
                instance.setBlock(blockPos, Block.GRASS_BLOCK);
                instance.setBlock(blockPos.add(0, -1, 0), Block.END_ROD);
            });

        } else {
            ParkourBlockThread parkourBlockThread = new ParkourBlockThread();
            parkourBlockThread.start(player);
            parkourBlockThread.getQueue().addAll(blockList);
            Pos lastPos = blockList.getLast();
            blockList.clear();
            player.sendMessage("Re-Calculating…");
            generateSequence(player, lastPos);

            float health = player.getHealth();
            if(health < 20) {
                player.setHealth(health+1);
                player.playSound(Sound.sound(SoundEvent.ENTITY_SPLASH_POTION_THROW, Sound.Source.BLOCK, 1f, 1f));
            }
        }
    }

    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (!isBlocked) {
            isBlocked = true;
            generateSequence(player);

        } else if (shouldRecalculate(player.getPosition())) {
            isBlocked = false;
            iteration++;
            player.sendMessage(Component.text("Check point set", NamedTextColor.GREEN));
        }

        // Teleport on fail
        if (player.getPosition().y() < teleportThresholdAdjusted) {
            Teleport(player);
            handleHealth();
        }
    }

    public void generateSequence(Player player) {
        generateSequence(player, getBlockPosUnderPlayer(player.getPosition()));
    }

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

    public boolean isFirstIteration() {
        return iteration == 1;
    }

    private Pos getBlockPosUnderPlayer(Pos pos) {
        return pos.add(0, -1, 0);
    }

    public boolean shouldRecalculate(Pos playerPos) {
        return CoordinateUtil.comparePos(playerPos, blockList.get
                (SEQUENCE_SIZE - (3 + 1)) // 4 blocks before end
        );
    }

    private void Teleport(Player player) {
        if (iteration >= 2) {
            Pos blockPos = blockList.getFirst().add(0, 1, 0);
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

    public void undo() {
        for (Pos pos : blockList) {
            Parkour.getInstance().getInstances().getFirst().setBlock(pos, Block.AIR);
            Parkour.getInstance().getInstances().getFirst().setBlock(pos.add(0, -1, 0), Block.AIR);
        }
    }

    private void setupScoreboard() {
        this.sidebar = new Sidebar(Component.text(" – JumpBoard – ", NamedTextColor.DARK_AQUA));
        Sidebar.ScoreboardLine line = new Sidebar.ScoreboardLine(
                "progress",
                Component.text("Progress ", NamedTextColor.GRAY).append(Component.text(iteration, NamedTextColor.YELLOW)),
                0
        );
        this.sidebar.createLine(line);
        this.sidebar.addViewer(player);
    }

    private void updateScoreboard() {
        this.sidebar.updateLineScore("progress", iteration);
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
}
