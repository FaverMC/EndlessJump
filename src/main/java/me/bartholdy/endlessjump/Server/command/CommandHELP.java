package me.bartholdy.endlessjump.Server.command;

import me.bartholdy.endlessjump.Server.util.MessageUtil;
import net.minestom.server.command.builder.Command;

public class CommandHELP extends Command {
    public CommandHELP() {
        super("help");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(MessageUtil.LAUNCH_MESSAGE);
        });
    }
}
