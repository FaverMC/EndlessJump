package me.bartholdy.endlessjump.Server.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;

public class CommandHELP extends Command {
    public CommandHELP() {
        super("help");

        setDefaultExecutor((sender, context) -> {
        });
    }

    private Component GetAboutCommands() {
        return Component.text()
                .appendNewline()
                .append(text(""))
                .build();
    }
}
