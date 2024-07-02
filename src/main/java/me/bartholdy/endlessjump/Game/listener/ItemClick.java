package me.bartholdy.endlessjump.Game.listener;

import me.bartholdy.endlessjump.Game.Parkour;
import me.bartholdy.endlessjump.Game.ParkourBlocks;
import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.player.PlayerEatEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemClick implements EventListener<PlayerUseItemEvent> {
    @Override
    public @NotNull Class<PlayerUseItemEvent> eventType() {
        return PlayerUseItemEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull PlayerUseItemEvent event) {
        ParkourBlocks player = ((ParkourPlayer) event.getPlayer()).getParkourBlocks();
        ItemStack item = event.getItemStack();
        if (item.isAir()) return Result.SUCCESS;
        if (item.equals(Parkour.getSupportItem()))
            player.onSupport();
        if (item.equals(Parkour.getResetItem()))
            player.onReset();
        if (item.equals(Parkour.getQuitItem()))
            player.onQuit();

        return Result.SUCCESS;
    }
}
