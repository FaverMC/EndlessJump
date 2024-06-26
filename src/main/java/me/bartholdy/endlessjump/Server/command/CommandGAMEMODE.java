package me.bartholdy.endlessjump.Server.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.condition.CommandCondition;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CommandGAMEMODE extends Command {
    public CommandGAMEMODE() {
        super("gamemode", "gm");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text(
                    "Usage: /gamemode <mode>", NamedTextColor.RED)
            );
            sender.sendMessage(Component.text(
                    "Usage: /gamemode <mode> <player>", NamedTextColor.RED)
            );
        });

        var testArgument = ArgumentType.Integer("mode").between(0, 3);
        var targetArgument = ArgumentType.Entity("player").onlyPlayers(true);

        // Player not found
        targetArgument.setCallback(((sender, exception) -> {
            sender.sendMessage(Component.text("Player " + exception.getInput() + " not found").color(NamedTextColor.RED));
        }));

        // Mode not found
        testArgument.setCallback(((sender, exception) -> {
            sender.sendMessage(Component.text("The fuck you want what?").color(NamedTextColor.RED));
        }));

        // For executor only
        addSyntax(((sender, context) -> {
            if (sender instanceof Player player) {
                final int gamemodeID = context.get("mode");
                if (gamemodeID < 0 || gamemodeID > 3) {
                    sender.sendMessage(Component.text("Unknown game mode id: " + gamemodeID).color(NamedTextColor.RED));
                    return;
                }
                final GameMode gameMode = GameMode.fromId(gamemodeID);
                player.setGameMode(gameMode);
                player.sendMessage(Component.text("Game mode changed to " + getGameModeNameFromID(gamemodeID), NamedTextColor.GREEN));

            } else {
                sender.sendMessage(Component.text("Only players can use this command!"));
            }
        }), testArgument);

        // For target and executor
        addSyntax(((sender, context) -> {
            final int gamemodeID = context.get("mode");
            if (gamemodeID < 0 || gamemodeID > 3) {
                sender.sendMessage(Component.text("Unknown game mode id: " + gamemodeID).color(NamedTextColor.RED));
                return;
            }
            final GameMode gameMode = GameMode.fromId(gamemodeID);
            final EntityFinder entityFinder = context.get("player");
            Player player = entityFinder.findFirstPlayer(sender);
            assert player != null;
            player.setGameMode(gameMode);
            player.sendMessage(Component.text("Your game mode has been changed", NamedTextColor.GREEN));
            sender.sendMessage(Component.text("Game mode changed successfully for " + player.getUsername(), NamedTextColor.GREEN));
        }), testArgument, targetArgument);
    }

    private String getGameModeNameFromID(int modeID) {
        switch (modeID) {
            case 0 -> {
                return "Adventure";
            }
            case 1 -> {
                return "Creative";
            }
            case 2 -> {
                return "Survival";
            }
            case 3 -> {
                return "Spectate";
            }
            default -> {
                return "Unable";
            }
        }
    }
}
