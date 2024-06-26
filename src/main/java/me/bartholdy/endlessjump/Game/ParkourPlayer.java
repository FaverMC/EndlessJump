package me.bartholdy.endlessjump.Game;

import lombok.Getter;
import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ParkourPlayer extends Player {

    @Getter // created on login
    private ParkourBlocks parkourBlocks;

    public ParkourPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
        this.parkourBlocks = new ParkourBlocks(this);
    }

    // Level bestimmen, anzeigen

    // Extra Punkte für streaks

    // Extrapunkte für Streaks wie weit sinnvoll?
    // Es ist nicht schwer streaks zu erreichen.
    // Sollen die Streaks erst bei hoher Iteration
    // beginnen? Bspw. ab 50 ?
}
