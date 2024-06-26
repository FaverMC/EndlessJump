package me.bartholdy.endlessjump.Game;

import lombok.Getter;
import me.bartholdy.endlessjump.Game.listener.AsyncPlayerConfiguration;
import me.bartholdy.endlessjump.Game.listener.PlayerDisconnect;
import me.bartholdy.endlessjump.Game.listener.PlayerMove;
import me.bartholdy.endlessjump.Game.listener.PlayerSpawn;
import me.bartholdy.endlessjump.Server.Main;
import me.bartholdy.endlessjump.Server.plugin.Plugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parkour extends Plugin {
    private static final ComponentLogger LOGGER = ComponentLogger.logger(Parkour.class);
    @Getter
    private static Parkour instance;
    @Getter
    private JedisPooled jedis;
    @Getter
    private ParkourDatabase parkourDatabase;

    public Parkour(Main server) {
        super(server);
        instance = this;
        jedis = getJedisPooled();
        try {
            parkourDatabase = new ParkourDatabase(jedis, "parkour");
        } catch (Exception e) {
            LOGGER.error(Component.text("Failed to create a parkour database", NamedTextColor.RED));
        }
    }

    @Override
    public void Enable() {
        MinecraftServer.getConnectionManager().setPlayerProvider(ParkourPlayer::new);

        eventNode
                .addListener(new AsyncPlayerConfiguration())
                .addListener(new PlayerSpawn())
                .addListener(new PlayerDisconnect())
                .addListener(new PlayerMove())
                .addListener(PlayerSwapItemEvent.class, event -> event.setCancelled(true)); // only main hand allowed
    }

    @Override
    public void Disable() {
    }

    @Override
    public String getName() {
        return "Jump";
    }

    @Override
    protected Command initCommands(Command rootCommand) {
        return null;
    }

    @Override
    protected ArrayList<InstanceContainer> initInstances() throws IOException {
        Pos spawnPos = new Pos(0.5, 69, 0.5, -45, 0);
        InstanceContainer instanceContainer = MinecraftServer.getInstanceManager().createInstanceContainer();
        instanceContainer.setChunkLoader(new AnvilLoader("worlds/" + getName()));
        instanceContainer.setWorldSpawnPosition(spawnPos, true);
        instanceContainer.setBlock(spawnPos.add(0, -1, 0), Block.EMERALD_BLOCK);
        instanceContainer.setChunkSupplier(LightingChunk::new);
        instanceContainer.setTimeRate(0);
        instanceContainer.setTime(20000);
        return new ArrayList<>(List.of(instanceContainer));
    }

    @NotNull
    private JedisPooled getJedisPooled() {
        return new JedisPooled("localhost", 6379);
    }

    private static ItemStack supportItem;
    private static ItemStack resetItem;
    private static ItemStack quitItem;

    public static ItemStack getSupportItem() {
        if (supportItem == null)
            supportItem = ItemStack.builder(Material.SLIME_BALL).displayName(Component.text("Support", NamedTextColor.GREEN)).build();
        return supportItem;
    }

    public static ItemStack getResetItem() {
        if (resetItem == null)
            resetItem = ItemStack.builder(Material.SLIME_BALL).displayName(Component.text("Reset", NamedTextColor.YELLOW)).build();
        return resetItem;
    }

    public static ItemStack getQuitItem() {
        if (quitItem == null)
            quitItem = ItemStack.builder(Material.SLIME_BALL).displayName(Component.text("Back", NamedTextColor.RED)).build();
        return quitItem;
    }
}
