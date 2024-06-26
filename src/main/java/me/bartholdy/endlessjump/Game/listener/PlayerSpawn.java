package me.bartholdy.endlessjump.Game.listener;

import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

public class PlayerSpawn implements EventListener<PlayerSpawnEvent> {

    @Override
    public @NotNull Class<PlayerSpawnEvent> eventType() {
        return PlayerSpawnEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull PlayerSpawnEvent event) {
        ParkourPlayer player = (ParkourPlayer) event.getPlayer();
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20F);
        player.setBoots(ItemStack.builder(Material.GOLDEN_BOOTS).displayName(Component.text("SKECHERS", NamedTextColor.LIGHT_PURPLE)).build());

        return Result.SUCCESS;
    }
}
