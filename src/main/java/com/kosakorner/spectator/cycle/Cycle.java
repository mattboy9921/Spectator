package com.kosakorner.spectator.cycle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Cycle {

    private Player owner;
    private List<Player> alreadyVisited = new ArrayList<>();
    private List<Player> toVisit = new ArrayList<>();

    private Random random = new Random();

    public Cycle(Player owner) {
        this.owner = owner;
    }

    public boolean hasNextPlayer() {
        return alreadyVisited.size() != toVisit.size();
    }

    public Player getNextPlayer() {
        updateLists();
        Player player = toVisit.get(random.nextInt(toVisit.size()));
        alreadyVisited.remove(player);
        return player;
    }

    private void updateLists() {
        toVisit = new ArrayList<>(Bukkit.getOnlinePlayers());

        // Clear the toVisit list of players that have been visited.
        for (Player player : alreadyVisited) {
            if (!player.isOnline()) {
                alreadyVisited.remove(player);
            }
            toVisit.remove(player);
        }
        toVisit.remove(owner);
    }

}
