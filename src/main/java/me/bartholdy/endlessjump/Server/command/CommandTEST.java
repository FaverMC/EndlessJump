package me.bartholdy.endlessjump.Server.command;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class CommandTEST extends Command {

    public CommandTEST() {
        super("test");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("Usage: /command <number>");
        });

        var numberArgument = ArgumentType.Integer("my-number");

        // Callback executed if the argument has been wrongly used
        numberArgument.setCallback((sender, exception) -> {
            final String input = exception.getInput();
            sender.sendMessage("The number " + input + " is invalid!");
        });

        addSyntax((sender, context) -> {
            final int number = context.get(numberArgument);
            sender.sendMessage("You typed the number " + number);
        }, numberArgument);

    }
}