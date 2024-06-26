package me.bartholdy.endlessjump.Server.plugin;

import lombok.Getter;
import lombok.Setter;
import me.bartholdy.endlessjump.Server.Main;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Plugin {
    public static final Logger logger;
    /**
     * -- GETTER --
     * Get the events associated with this plugin
     */
    public @NotNull EventNode<Event> eventNode;
    /**
     * -- GETTER --
     * Get the instances associated with this plugin
     */
    @Getter
    private ArrayList<InstanceContainer> instances;
    @Getter
    @Setter
    private Main server;

    static {
        logger = LoggerFactory.getLogger(Plugin.class);
    }

    protected Plugin(Main main) {
        server = main;

        // Init instances from inherent class
        instances = new ArrayList<>();
        try {
            instances = initInstances();
        } catch (IOException e) {
            logger.error("Failed to load instances for plugin '" + getName() + "'");
            throw new RuntimeException(e);
        }
    }

    public void Enable() {
    }

    public void Disable() {
    }

    public void onEnable() {
        eventNode = EventNode.all("demo");

        Enable();
        logger.info("Plugin " + getName() + " successfully started");
    }

    public void onDisable() {
        Disable();
    }

    public abstract String getName();

    protected abstract Command initCommands(Command rootCommand);

    protected abstract ArrayList<InstanceContainer> initInstances() throws IOException;
}
