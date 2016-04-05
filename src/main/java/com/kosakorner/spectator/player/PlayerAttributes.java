package com.kosakorner.spectator.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Store player attributes while spectating.
 */
public class PlayerAttributes {

    private GameMode gameMode;
    private Location location;

    public PlayerAttributes(Player player) {
        gameMode = player.getGameMode();
        location = player.getLocation();
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Location getLocation() {
        return location;
    }

}
