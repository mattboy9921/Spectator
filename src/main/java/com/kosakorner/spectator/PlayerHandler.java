package com.kosakorner.spectator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class PlayerHandler implements Listener {

    private Plugin plugin;

    public PlayerHandler(Plugin plugin) {
        this.plugin = plugin;
    }

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
                    Spectator.spectators.remove(player);
                }
            }
        }
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClick(InventoryClickEvent event) {
        resendInventoryToSpectators((Player) event.getWhoClicked());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryDrag(InventoryDragEvent event) {
        resendInventoryToSpectators((Player) event.getWhoClicked());
    }

    private void resendInventoryToSpectators(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectators.entrySet()) {
                    if (entry.getValue().equals(player)) {
                        Player target = entry.getKey();
                        InventoryManager.resendInventoy(player, target);
                    }
                }
            }
        });
    }

}
