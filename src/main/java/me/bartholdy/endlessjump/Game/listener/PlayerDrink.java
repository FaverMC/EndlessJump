package me.bartholdy.endlessjump.Game.listener;

import me.bartholdy.endlessjump.Game.ParkourPlayer;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.entity.EntityPotionAddEvent;
import org.jetbrains.annotations.NotNull;

/**
 * TODO Prevent drinking milk from bucket FIX
 */
public class PlayerDrink implements EventListener<EntityPotionAddEvent> {
    @Override
    public @NotNull Class<EntityPotionAddEvent> eventType() {
        return EntityPotionAddEvent.class;
    }

    @NotNull
    @Override
    public Result run(@NotNull EntityPotionAddEvent event) {
        if (event.getEntity() instanceof ParkourPlayer player) {
            player.sendMessage("Yummy");
        }
        return Result.SUCCESS;
    }
}
