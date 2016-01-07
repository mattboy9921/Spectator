package com.kosakorner.spectator;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SpectateCommand implements CommandExecutor {

    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0 && sender.hasPermission(Spectator.PERM_TELEPORT)) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (target.getUniqueId().equals(player.getUniqueId())) {
                        sender.sendMessage(ChatColor.RED + "You can't spectate yourself!");
                        return true;
                    }
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(target, PlayerTeleportEvent.TeleportCause.SPECTATE);
                    player.setSpectatorTarget(target);
                    if (player.hasPermission(Spectator.PERM_INVENTORY)) {
                        InventoryManager.swapInventories(player, target);
                    }
                    Spectator.spectators.put(player, target);
                    sender.sendMessage(ChatColor.AQUA + "You are now spectating " + target.getName() + "!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + args[0] + " isn't online!");
                }
                return true;
            }
            else {
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.setGameMode(GameMode.SPECTATOR);
                    sender.sendMessage(ChatColor.AQUA + "You are now spectating!");
                    return true;
                }
            }
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
            player.setGameMode(GameMode.SURVIVAL);
            if (player.hasPermission(Spectator.PERM_INVENTORY)) {
                InventoryManager.restoreInventory(player);
            }
            Spectator.spectators.remove(player);
            sender.sendMessage(ChatColor.RED + "You are no longer spectating!");
        }
        else {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
        }
        return true;
    }

}
