package com.kosakorner.spectator.handler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class PlayerHandler implements Listener {

    private final Set<Player> hiddenPlayers = new HashSet<>();
    private final BiMap<Player, Location> lastLocationCache = HashBiMap.create();
    private final BiMap<Player, GameMode> lastGameModeCache = HashBiMap.create();

    public PlayerHandler() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Spectator.instance, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                    final Player player = entry.getKey();
                    final Player target = entry.getValue();
                    if (!player.getWorld().equals(target.getWorld()) || player.getLocation().distanceSquared(target.getLocation()) > 1) {
                        player.setSpectatorTarget(null);
                        player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
                        Bukkit.getScheduler().runTaskLater(Spectator.instance, new Runnable() {
                            @Override
                            public void run() {
                                player.setSpectatorTarget(target);
                            }
                        }, 5);
                    }
                }
            }
        }, 0, 20);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Spectator.instance, new Runnable() {
            @Override
            public void run() {
                for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
                    InventoryHandler.resendInventoy(entry.getValue(), entry.getKey());
                }
            }
        }, 0, 15);
    }

    public void spectatePlayer(final Player player, final Player target) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
            lastLocationCache.forcePut(player, player.getLocation());
            lastGameModeCache.forcePut(player, player.getGameMode());
        }
        player.setGameMode(GameMode.SPECTATOR);
        if (!Spectator.trackedSpectators.contains(player)) {
            Spectator.trackedSpectators.add(player);
        }
        if (Config.hideFromTab) {
            updatePlayerVisibility(player, false);
        }
        if (target != null) {
            if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
                InventoryHandler.restoreInventory(player);
                InventoryHandler.swapInventories(player, target);
            }
            Spectator.spectatorRelations.remove(player);
            Spectator.spectatorRelations.put(player, target);
            player.setSpectatorTarget(null);
            player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Bukkit.getScheduler().runTaskLater(Spectator.instance, new Runnable() {
                @Override
                public void run() {
                    player.setSpectatorTarget(target);
                }
            }, 5);
        }
    }

    @SuppressWarnings("deprecation")
    public void unspectatePlayer(final Player player) {
        // Check if the location is safe.
        Location location = null;
        if (Config.rememberSurvivalPosition) {
            location = lastLocationCache.get(player);
        }
        if (location == null) {
            location = player.getLocation();
            float pitch = location.getPitch();
            float yaw = location.getYaw();
            if (!location.getBlock().getType().equals(Material.AIR) || !player.isOnGround()) {
                location = location.getWorld().getHighestBlockAt(location).getLocation();
                location.setPitch(pitch);
                location.setYaw(yaw);
            }
        }
        player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        Spectator.trackedSpectators.remove(player);
        Spectator.spectatorRelations.remove(player);
        if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
            InventoryHandler.restoreInventory(player);
        }
        if (Config.hideFromTab) {
            updatePlayerVisibility(player, true);
        }
        GameMode gameMode = lastGameModeCache.get(player);
        player.setGameMode(gameMode);
    }

    public void updatePlayerVisibility(Player player, boolean show) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (!target.getUniqueId().equals(player.getUniqueId())) {
                if (!Spectator.hasPermission(target, Permissions.BYPASS_TABLIST)) {
                    if (show) {
                        hiddenPlayers.remove(player);
                        target.showPlayer(player);
                    }
                    else {
                        hiddenPlayers.add(player);
                        target.hidePlayer(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Spectator.hasPermission(player, Permissions.BYPASS_TABLIST)) {
            for (Player hidden : hiddenPlayers) {
                player.hidePlayer(hidden);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        hiddenPlayers.remove(player);
        lastLocationCache.remove(player);
        lastGameModeCache.remove(player);
        for (Map.Entry<Player, Player> entry : Spectator.spectatorRelations.entrySet()) {
            if (entry.getValue().equals(player)) {
                Player spectator = entry.getKey();
                if (!Spectator.cycleHandler.isPlayerCycling(spectator)) {
                    dismountTarget(spectator);
                }
                else {
                    Spectator.cycleHandler.restartCycle(spectator);
                }
            }
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
            // Only capture the button down event.
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

    public void dismountTarget(Player player) {
        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
            if (player.getSpectatorTarget() != null && player.getSpectatorTarget().getType().equals(EntityType.PLAYER)) {
                if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
                    InventoryHandler.restoreInventory(player);
                }
                Spectator.spectatorRelations.remove(player);
                player.setSpectatorTarget(player);
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
        if (event.getWhoClicked().getGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked().getGameMode().equals(GameMode.SPECTATOR)) {
            event.setCancelled(true);
        }
    }

    public void restoreAllSpectators() {
        for (Player player : Spectator.trackedSpectators) {
            unspectatePlayer(player);
        }
    }

}
