package me.bartholdy.endlessjump.Server;

import lombok.Getter;
import me.bartholdy.endlessjump.Game.Parkour;
import me.bartholdy.endlessjump.Server.command.*;
import me.bartholdy.endlessjump.Server.plugin.PluginManager;
import me.bartholdy.endlessjump.Server.util.FileResult;
import me.bartholdy.endlessjump.Server.util.FileUtil;
import me.bartholdy.endlessjump.Server.util.ObjectTriple;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.extras.bungee.BungeeCordProxy;
import net.minestom.server.extras.lan.OpenToLAN;
import net.minestom.server.extras.velocity.VelocityProxy;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Duration;

import static net.kyori.adventure.text.Component.text;

public class Main {
    @Getter
    private static Main instance;
    private static final ComponentLogger LOGGER = ComponentLogger.logger(Main.class);
    public static final String CONFIG_LOCATION = System.getProperty("config.location", "server.json");
    public static final String LAUNCHER_VERSION_NAME = "1.0.0";
    private InstanceContainer defaultInstanceContainer;
    @Getter
    private PluginManager pluginManager;

    public static void main(String[] args) {
        new Main().init();
    }

    public void init() {
        instance = this;
        LOGGER.info("Loading configuration…");
        loadConfiguration();
    }

    private void loadConfiguration() {
        long startMillis = System.currentTimeMillis();
        ObjectTriple<FileResult, Config, Exception> triple = FileUtil.loadConfig(Path.of(CONFIG_LOCATION));

        switch (triple.left()) {
            case CREATED, EXISTING -> start(startMillis, triple.left(), triple.mid());
            case MALFORMED -> shutdown();
        }
    }

    private void start(long startMillis, @NotNull FileResult result, @NotNull Config Config) {
        Config.Network networkData = Config.networkData();
        Config.Proxy proxyData = Config.proxyData();
        Config.Server serverData = Config.serverData();
        Config.Instance instanceData = Config.instanceData();
        Config.Commands commandData = Config.commandsData();

        System.setProperty("minestom.tps", String.valueOf(serverData.ticksPerSecond()));
        System.setProperty("minestom.chunk-view-distance", String.valueOf(serverData.chunkViewDistance()));
        System.setProperty("minestom.entity-view-distance", String.valueOf(serverData.entityViewDistance()));

        MinecraftServer minecraftServer = MinecraftServer.init();

        if (networkData.openToLan()) {
            OpenToLAN.open();
        }

        // proxy
        if (proxyData.enabled()) {
            String proxyType = proxyData.type();
            if (proxyType.equalsIgnoreCase("velocity")) {
                VelocityProxy.enable(proxyData.secret());
            } else if (proxyType.equalsIgnoreCase("bungeecord")) {
                BungeeCordProxy.enable();
            }
        } else if (serverData.onlineMode()) {
            MojangAuth.init();
        }

        if (serverData.benchmark()) {
            MinecraftServer.getBenchmarkManager().enable(Duration.of(10, TimeUnit.SECOND));
        }

        // instance – world
        if (instanceData.enabled()) {
            LOGGER.info(text("Preparing default instance from config…"));
            InstanceManager instanceManager = MinecraftServer.getInstanceManager();
            InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
            String defaultWorldName = instanceData.worldName();
            Pos customSpawnPosition = new Pos(55.5, 65, 68.5, -180, 0);

            instanceContainer.setChunkLoader(new AnvilLoader("worlds/" + defaultWorldName));
            instanceContainer.setWorldSpawnPosition(customSpawnPosition, true);
            instanceContainer.setBlock(customSpawnPosition.add(0, -2, 0), Block.GRASS_BLOCK);
            defaultInstanceContainer = instanceContainer;
            MinecraftServer.getInstanceManager().registerInstance(instanceContainer);

            LOGGER.info(customSpawnPosition.toString());

            // basic player configuration (on join)
            MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
                Player player = event.getPlayer();
                event.setSpawningInstance(instanceContainer);
                player.setRespawnPoint(customSpawnPosition);
            });
        }

        initCommands(commandData);

        MinecraftServer.getSchedulerManager()
                .buildShutdownTask(() -> {
		        	stop();
                    System.out.println("Adios " + System.getProperty("user.name"));
                });

        minecraftServer.start(networkData.ip(), networkData.port());
        // Set brand name (visible at F3)
//        MinecraftServer.setBrandName("FaverMC");
//        MinecraftServer.setBrandName("lig-ma-balls");
        MinecraftServer.setBrandName("Ligma");

        LOGGER.debug("Set server brand");
        LOGGER.info("Server started");

        pluginManager = new PluginManager();
        pluginManager.enablePlugin(new Parkour(this));
    }

    private void initCommands(Config.Commands commandData) {
        CommandManager commandManager = MinecraftServer.getCommandManager();
        if (commandData.give())
            commandManager.register(new CommandGIVE());
        if (commandData.gamemode())
            commandManager.register(new CommandGAMEMODE());
        if (commandData.test())
            commandManager.register(new CommandTEST());
        if (commandData.help())
            commandManager.register(new CommandHELP());
        if (commandData.command())
            commandManager.register(new CommandCOMMANDS());
        if (commandData.plugins())
            commandManager.register(new CommandPLUGINS());

        commandManager.setUnknownCommandCallback((sender, command) -> {
            if (command.isEmpty())
                return;
//            else if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("?"))
//                sender.sendMessage(text(
//                        "Commands (" + commandManager.getCommands().size() + "): "
//                                + commandManager.getCommands().stream().map(s -> s.getName())
//                                .collect(Collectors.toList()).toString().replace("[", "").replaceAll("]", ""),
//                        NamedTextColor.RED));
//            else
            sender.sendMessage(text("Unknown command: /" + command, NamedTextColor.RED));
        });

    }

    private void stop() {
//        System.out.println("Saving chunks for lobby…");
//        defaultInstanceContainer.saveChunksToStorage();
        MinecraftServer.stopCleanly();
    }

    private void shutdown() {
        LOGGER.info(
                """
                        Error while launching the Minestom server (check and verify server.json config file)
                        For example:
                            - Check for spelling and syntax
                            - You cannot rearrange lines of text
                        Shutting down in 5 seconds…
                        """);
        try {
            Thread.sleep(5000);
        } catch (IllegalArgumentException | InterruptedException exc) {
            throw new RuntimeException(exc);
        }
    }

    public static String getJarName() {
        return new java.io.File(Main.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getName();
    }
}