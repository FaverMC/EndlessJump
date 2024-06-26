package me.bartholdy.endlessjump.Game;

import me.bartholdy.endlessjump.GameAPI.AbstractDatabase;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.mojang.MojangUtils;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.resps.Tuple;

import java.util.List;
import java.util.Objects;

public class ParkourDatabase extends AbstractDatabase {
    public ParkourDatabase(JedisPooled jedis, String nameSpace) {
        super(jedis, nameSpace);
    }

    public boolean hasPlayed(Player player) {
        return jedis.exists(nameSpace + "playTime:" + player.getUsername());
    }

    public long getPlayTime(Player player) {
        return Long.parseLong(jedis.get(nameSpace + "playTime:" + player.getUsername()));
    }

    public void setPlayTime(Player player, long playTime) {
        jedis.set(nameSpace + "playTime:" + player.getUsername(), String.valueOf(playTime));
    }

    public void addPlayTime(Player player, long playTime) {
        jedis.set(nameSpace + "playTime:" + player.getUsername(), String.valueOf(Double.sum(getPlayTime(player), playTime)));
    }

    public int getStreak(Player player) {
        return Integer.parseInt(jedis.get(nameSpace + "streak:" + player.getUsername()));
    }

    public void setStreak(Player player, int streak) {
        jedis.set(nameSpace + "streak:" + player.getUsername(), String.valueOf(streak));
    }

    public void addStreak(Player player, int streak) {
        jedis.set(nameSpace + "streak:" + player.getUsername(), String.valueOf(Integer.sum(getStreak(player), streak)));
    }

    public int getScore(Player player) {
        return Integer.parseInt(jedis.get(nameSpace + "score:" + player.getUsername()));
    }

    public void setScore(Player player, int score) {
        jedis.set(nameSpace + "score:" + player.getUsername(), String.valueOf(score));
    }

    public void addScore(Player player, int score) {
        jedis.set(nameSpace + "score:" + player.getDisplayName(), String.valueOf(Integer.sum(getScore(player), score)));
    }

    public List<Tuple> getTopStreak(int maxRange) {
        return getTopStreak("streak", 0, maxRange);
    }

    public List<Tuple> getTopScore(int maxRange) {
        return getTopStreak("score", 0, maxRange);
    }

    public List<Tuple> getTopPlayTime(int maxRange) {
        return getTopStreak("playTime", 0, maxRange);
    }

    private List<Tuple> getTopStreak(String key, int minRange, int maxRange) {
        List<Tuple> databaseTuple = jedis.zrangeWithScores(nameSpace + ":" + key, minRange, maxRange);
        return databaseTuple.stream()
                .map((tuple ->
                        new Tuple(Objects.requireNonNull(MojangUtils.fromUuid(tuple.getElement())).get("name").getAsString(),
                                tuple.getScore()))
                ).toList();
    }

    /**
     * On block change event
     */
    public long getDistanceTraveled(Player player) {
        return Long.parseLong(jedis.get(nameSpace + "distanceTraveled:" + player.getUsername()));
    }

    public void setDistanceTraveled(Player player, long distanceTraveled) {
        jedis.set(nameSpace + "distanceTraveled:" + player.getUsername(), String.valueOf(distanceTraveled));
    }

    public void addDistanceTraveled(Player player, long distanceTraveled) {
        jedis.set(nameSpace + "distanceTraveled:" + player.getUsername(), String.valueOf(Long.sum(getDistanceTraveled(player), distanceTraveled)));
    }

    public void reset(Player player) {
        jedis.del(nameSpace + "playTime:" + player.getUsername());
        jedis.del(nameSpace + "streak:" + player.getUsername());
        jedis.del(nameSpace + "score:" + player.getUsername());
    }
}
