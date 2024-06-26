package me.bartholdy.endlessjump.Server.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandCOMMANDS extends Command {
    public CommandCOMMANDS() {
        super("commands");
        setDefaultExecutor((sender, context) -> {
            var pluginCount = MinecraftServer.getCommandManager().getCommands().size();
            sender.sendMessage(Component.text(
                    "Commands (" + pluginCount + "): " + GetCommandList()).color(NamedTextColor.RED));
        });
    }

    private String GetCommandList() {
        return MinecraftServer.getCommandManager().getCommands().stream()
                .map(Command::getName)
                .toList()
                .toString()
                .replace("[", "")
                .replaceAll("]", "");
    }
}
