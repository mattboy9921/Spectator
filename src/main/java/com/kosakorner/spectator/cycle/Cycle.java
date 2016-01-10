package com.kosakorner.spectator.cycle;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cycle {

    private Player owner;
    private Player last;
    private List<Player> alreadyVisited = new ArrayList<>();
    private List<Player> toVisit = new ArrayList<>();

    private Random random = new Random();

    public Cycle(Player owner, Player last) {
        this.owner = owner;
        this.last = last;
    }

    public boolean hasNextPlayer() {
        return alreadyVisited.size() != toVisit.size();
    }

    public Player getLastPlayer() {
        return last;
    }

    public Player getNextPlayer() {
        updateLists();
        if (toVisit.size() == 0) {
            return null;
        }
        if (toVisit.size() == 1) {
            return toVisit.get(0);
        }
        Player player = toVisit.get(random.nextInt(toVisit.size()));
        if (player.equals(last)) {
            return getNextPlayer();
        }
        last = player;
        alreadyVisited.add(player);
        return player;
    }

    private void updateLists() {
        toVisit = new ArrayList<>(Bukkit.getOnlinePlayers());
        for (Player player : toVisit) {
            if (player.hasPermission(Permissions.BYPASS_VIEWABLE)) {
                toVisit.remove(player);
            }
        }
        // Clear the toVisit list of players that have been visited.
        for (Player player : alreadyVisited) {
            if (!player.isOnline()) {
                alreadyVisited.remove(player);
            }
            toVisit.remove(player);
        }
        toVisit.remove(owner);
        for (Player player : Spectator.trackedSpectators) {
            toVisit.remove(player);
        }
    }

}
