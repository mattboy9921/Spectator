package com.kosakorner.spectator;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class PlayerHandler implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation,unused")
    public void onPlayerDismount(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            // Only capture the button down event
            if (event.isSneaking()) {
                if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                    if (player.hasPermission(Spectator.PERM_INVENTORY)) {
                        InventoryManager.restoreInventory(player);
                    }
                }
            }
        }
    }

}
