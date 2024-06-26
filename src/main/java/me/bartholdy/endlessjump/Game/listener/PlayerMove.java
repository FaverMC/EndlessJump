package me.bartholdy.endlessjump.Game.listener;

import me.bartholdy.endlessjump.Game.Parkour;
import me.bartholdy.endlessjump.Game.ParkourBlocks;
import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerMoveEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerMove implements EventListener<PlayerMoveEvent> {

    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerMoveEvent event) {
        ((ParkourPlayer) event.getPlayer()).getParkourBlocks().onMove(event);
        return Result.SUCCESS;
    }
}