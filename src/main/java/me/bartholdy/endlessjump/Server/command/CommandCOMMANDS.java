package me.bartholdy.endlessjump.Server.command;

import me.bartholdy.endlessjump.Server.util.MessageUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class CommandCOMMANDS extends Command {
    public CommandCOMMANDS() {
        super("commands");
        setDefaultExecutor((sender, context) -> {
            var pluginCount = MinecraftServer.getCommandManager().getCommands().size();
            sender.sendMessage(Component.text(
                    "Commands (" + pluginCount + "): " + MessageUtil.GetCommandList()).color(NamedTextColor.RED));
        });
    }
}
