package com.kosakorner.spectator.cycle;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

public class CycleHandler {

    private Map<Player, CycleTask> cycleTasks = new HashMap<>();
    private Map<Player, Cycle> playerCycles = new HashMap<>();

    public boolean isPlayerCycling(Player player) {
        return playerCycles.containsKey(player);
    }

    public void startCycle(final Player player, int ticks) {
        playerCycles.put(player, new Cycle(player, null));
        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Spectator.instance, new Runnable() {
            @Override
            public void run() {
                Cycle cycle = playerCycles.get(player);
                if (!cycle.hasNextPlayer()) {
                    Player last = cycle.getLastPlayer();
                    cycle = new Cycle(player, last);
                    playerCycles.put(player, cycle);
                }
                Player next = cycle.getNextPlayer();
                if (next != null) {
                    Spectator.playerListener.spectatePlayer(player, next);
                }
                else {
                    stopCycle(player);
                }
            }
        }, 0, ticks);
        cycleTasks.put(player, new CycleTask(task, ticks));
    }

    public void stopCycle(Player player) {
        cycleTasks.get(player).getTask().cancel();
        cycleTasks.remove(player);
        playerCycles.remove(player);
        Spectator.playerListener.dismountTarget(player);
        player.sendMessage(Messages.translate("Messages.Spectate.CycleStop"));
    }

    public void restartCycle(Player player) {
        CycleTask task = cycleTasks.get(player);
        task.getTask().cancel();
        cycleTasks.remove(player);
        startCycle(player, task.getInterval());
    }

    private class CycleTask {

        private BukkitTask task;
        private int interval;

        public CycleTask(BukkitTask task, int interval) {
            this.task = task;
            this.interval = interval;
        }

        public BukkitTask getTask() {
            return task;
        }

        public int getInterval() {
            return interval;
        }

    }

}
