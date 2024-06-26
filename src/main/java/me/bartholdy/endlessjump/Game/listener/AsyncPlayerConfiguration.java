package me.bartholdy.endlessjump.Game.listener;

import me.bartholdy.endlessjump.Game.Parkour;
import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerConfiguration implements EventListener<AsyncPlayerConfigurationEvent> {

    @Override
    public @NotNull Class<AsyncPlayerConfigurationEvent> eventType() {
        return AsyncPlayerConfigurationEvent.class;
    }

    @Override
    public @NotNull Result run(@NotNull AsyncPlayerConfigurationEvent event) {
        var parkourPlayer = (ParkourPlayer) event.getPlayer();
        var instance = Parkour.getInstance().getInstances().getFirst();
        event.setSpawningInstance(instance);
        parkourPlayer.setRespawnPoint(instance.getWorldSpawnPosition());
       return Result.SUCCESS;
    }
}
