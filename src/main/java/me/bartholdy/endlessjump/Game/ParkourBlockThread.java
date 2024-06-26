package me.bartholdy.endlessjump.Game;

import lombok.Getter;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.block.Block;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

public class ParkourBlockThread {
    private Timer regenrateTimer;

    @Getter
    private Queue<Pos> queue;

    private boolean shouldRun;

    private long speed = 400L;

    public void stop() {
        this.shouldRun = false;
    }

    public ParkourBlockThread() {
        this.queue = new LinkedList<>();
    }

    public ParkourBlockThread fast() {
        this.speed = 200L;
        return this;
    }

    public void start(Player player) {

        if (this.regenrateTimer != null)
            return;

        this.shouldRun = true;
        this.regenrateTimer = new Timer();
        this.regenrateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {

                if (!ParkourBlockThread.this.shouldRun) {
                    cancel();
                    return;
                }

                if (ParkourBlockThread.this.queue.peek() != null) {
                    final Pos blockPos = ParkourBlockThread.this.queue.poll();
                    Scheduler scheduler = player.scheduler();
                    Task task = scheduler.scheduleNextTick(() -> {

                        player.playSound(Sound.sound(SoundEvent.ENTITY_CREEPER_PRIMED, Sound.Source.RECORD, 1f, 1f));
                        player.getInstance().setBlock(blockPos, Block.REDSTONE_WIRE);
//                        SoundEvent.ENTITY_CREEPER_PRIMED
//                                ENTITY_EXPERIENCE_ORB_PICKUP
                    });
                }
            }
        }, 0L, speed);
    }
}
