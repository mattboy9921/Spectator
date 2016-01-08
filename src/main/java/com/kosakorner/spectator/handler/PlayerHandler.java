package com.kosakorner.spectator.handler;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

@SuppressWarnings("unused")
public class PlayerHandler implements Listener {

    private Plugin plugin;

    public PlayerHandler(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                    entry.getKey().teleport(entry.getValue(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                    entry.getKey().setSpectatorTarget(entry.getKey());
                    entry.getKey().setSpectatorTarget(entry.getValue());
                }
            }
        }, 0, 20);
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerDismount(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            // Only capture the button down event
            if (event.isSneaking()) {
                if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                    if (player.hasPermission(Permissions.INVENTORY)) {
                        InventoryHandler.restoreInventory(player);
                    }
                    Spectator.spectatorRelations.remove(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (Spectator.trackedSpectators.contains(player)) {
            player.sendMessage(Messages.translate("Messages.Player.GameModeBlocked"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        resendInventoryToSpectators((Player) event.getWhoClicked());
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        resendInventoryToSpectators((Player) event.getWhoClicked());
    }

    private void resendInventoryToSpectators(final Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                    if (entry.getValue().equals(player)) {
                        InventoryHandler.resendInventoy(player, entry.getKey());
                    }
                }
            }
        });
    }

}
