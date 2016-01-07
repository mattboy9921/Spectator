package com.kosakorner.spectator;

import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerHandler implements Listener {

    @EventHandler
    @SuppressWarnings("deprecation,unused")
    public void onPlayerDismount(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            // Only capture the button down event
            if (event.isSneaking()) {
                if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                    if (Spectator.showInventories && player.hasPermission(Spectator.PERM_INVENTORY)) {
                        player.getInventory().clear();
                        ItemStack[] items = Spectator.inventories.get(player);
                        Spectator.inventories.remove(player);
                        if (items != null) {
                            player.getInventory().setContents(items);
                        }
                        player.updateInventory();
                    }
                }
            }
        }
    }

}
