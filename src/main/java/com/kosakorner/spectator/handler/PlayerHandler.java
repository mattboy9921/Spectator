package com.kosakorner.spectator.handler;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
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

    public void spectatePlayer(Player player, Player target) {
        player.setGameMode(GameMode.SPECTATOR);
        if (!Spectator.trackedSpectators.contains(player)) {
            Spectator.trackedSpectators.add(player);
        }
        if (target != null) {
            player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
            player.setSpectatorTarget(target);
            if (player.hasPermission(Permissions.INVENTORY)) {
                InventoryHandler.restoreInventory(player);
                InventoryHandler.swapInventories(player, target);
            }
            Spectator.spectatorRelations.remove(player);
            Spectator.spectatorRelations.put(player, target);
        }
        if (Config.hideFromTab) {
            Spectator.packetHandler.hidePlayer(player);
        }
    }

    @SuppressWarnings("deprecation")
    public void unspectatePlayer(Player player) {
        // Check if the location is safe.
        Location location = player.getLocation();
        float pitch = location.getPitch();
        float yaw = location.getYaw();
        if (!location.getBlock().getType().equals(Material.AIR) || !player.isOnGround()) {
            location = location.getWorld().getHighestBlockAt(location).getLocation();
            location.setPitch(pitch);
            location.setYaw(yaw);
        }
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Spectator.trackedSpectators.remove(player);
        Spectator.spectatorRelations.remove(player);
        player.setGameMode(GameMode.SURVIVAL);
        if (player.hasPermission(Permissions.INVENTORY)) {
            InventoryHandler.restoreInventory(player);
        }
        if (Config.hideFromTab) {
            Spectator.packetHandler.showPlayer(player);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Config.cycleOnPlayerDeath) {
            Player player = event.getEntity();
            for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                if (entry.getValue().equals(player)) {
                    Player spectator = entry.getKey();
                    if (Spectator.cycleHandler.isPlayerCycling(spectator)) {
                        Spectator.cycleHandler.restartCycle(spectator);
                    }
                }
            }
        }
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onPlayerDismount(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (!Spectator.cycleHandler.isPlayerCycling(player)) {
            // Only capture the button down event
            if (event.isSneaking()) {
                dismountTarget(player);
            }
        }
        else {
            if (event.isSneaking()) {
                player.sendMessage(Messages.translate("Messages.Spectate.CycleNoDismount"));
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
            if (entry.getValue().equals(player)) {
                dismountTarget(entry.getKey());
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
            if (entry.getValue().equals(player)) {
                dismountTarget(entry.getKey());
            }
        }
    }

    public void dismountTarget(Player player) {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                if (player.hasPermission(Permissions.INVENTORY)) {
                    InventoryHandler.restoreInventory(player);
                }
                Spectator.spectatorRelations.remove(player);
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
        for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
            if (entry.getValue().equals(player)) {
                InventoryHandler.resendInventoy(player, entry.getKey());
            }
        }
    }

}
