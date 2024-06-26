package me.bartholdy.endlessjump.Game.listener;

import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDisconnect implements EventListener<PlayerDisconnectEvent> {
    @Override
    public @NotNull Class<PlayerDisconnectEvent> eventType() {
        return PlayerDisconnectEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull PlayerDisconnectEvent event) {
        ParkourPlayer player = (ParkourPlayer) event.getPlayer();
        player.getParkourBlocks().undo();
        return Result.SUCCESS;
    }
}