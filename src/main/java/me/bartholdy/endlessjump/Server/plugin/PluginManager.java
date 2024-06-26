package me.bartholdy.endlessjump.Server.plugin;

import me.bartholdy.endlessjump.Server.Main;
import net.minestom.server.MinecraftServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class PluginManager {

    private ArrayList<Plugin> plugins;

    public PluginManager() {
        plugins = new ArrayList<>();
    }

    public void enablePlugin(Plugin plugin) {
        plugin.onEnable();
        plugins.add(plugin);

        // Register the event node
        MinecraftServer.getGlobalEventHandler().addChild(plugin.eventNode);
    }

    public void disablePlugin(Plugin plugin) {
        plugin.onDisable();
        plugins.remove(plugin);
    }

    public void registerPlugin(Plugin plugin) {

    }

    public void registerPlugin(Plugin... plugins) {

    }

    public Optional<Plugin> getPlugin(Class<? extends Plugin> plugin) {
        return plugins.stream().filter(p -> p.getClass().getName().equals(plugin.getName())).findFirst();
    }

    public Collection<Plugin> getPlugins() {
        return plugins;
    }
}
