package me.bartholdy.endlessjump.Server.command;

import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.registry.ProtocolObject;
import net.minestom.server.utils.entity.EntityFinder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Nice to have for mods like
 * "Roughly Enough Items" or "Too Many Items"
 *
 */
public class CommandGIVE extends Command {
    public CommandGIVE() {
        super("give");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text(
                    "Usage: /give <player> <item>", NamedTextColor.RED)
            );
            sender.sendMessage(Component.text(
                    "Usage: /give <player> <item> <amount>", NamedTextColor.RED)
            );
        });

        var receiverArgument = ArgumentType.Entity("receiver").onlyPlayers(true);
        var amountArgument = ArgumentType.Integer("amount");
        var itemArgument = ArgumentType.ItemStack("item");

        // Player not found
        receiverArgument.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("Player " + exception.getInput() + " not found").color(NamedTextColor.RED));
        });

        // Item not found
        itemArgument.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("The item " + exception.getInput() + " does not exist").color(NamedTextColor.RED));
        });

        // Not a number
        amountArgument.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("Amount " + exception.getInput() + " is not a valid number").color(NamedTextColor.RED));
        });


        // Exec (3 args - with amount)
        addSyntax(((sender, context) -> {
            final ItemStack item = context.get(itemArgument);
            final EntityFinder entityFinder = context.get(receiverArgument);
            final int amount = context.get(amountArgument);
            Player player = entityFinder.findFirstPlayer(sender);
            assert player != null;
            player.getInventory().addItemStack(item.withAmount(amount));
        }), receiverArgument, itemArgument, amountArgument);

        // Exec (2 args)
        addSyntax(((sender, context) -> {
            final ItemStack item = context.get(itemArgument);
            final EntityFinder entityFinder = context.get(receiverArgument);
            Player player = entityFinder.findFirstPlayer(sender);
            assert player != null;
            player.getInventory().addItemStack(item);
        }), receiverArgument, itemArgument);
    }
}