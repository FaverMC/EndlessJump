package me.bartholdy.endlessjump.Server.command;

import me.bartholdy.endlessjump.Server.Main;
import me.bartholdy.endlessjump.Server.plugin.Plugin;
import me.bartholdy.endlessjump.Server.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandPLUGINS extends Command {
    public CommandPLUGINS() {
        super("plugins", "pl");

        setDefaultExecutor((sender, context) -> {
            var pluginCount = Main.getInstance().getPluginManager().getPlugins().size();
            sender.sendMessage(Component.text(
                    "Plugins (" + pluginCount + "): " + MessageUtil.GetPluginList()).color(NamedTextColor.RED));
        });
    }
}