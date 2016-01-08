package com.kosakorner.spectator.command;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import com.kosakorner.spectator.handler.InventoryHandler;
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
            if (args.length > 0 && sender.hasPermission(Permissions.TELEPORT)) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    if (target.getUniqueId().equals(player.getUniqueId())) {
                        sender.sendMessage(Messages.translate("Messages.Spectate.Self"));
                        return true;
                    }
                    player.setGameMode(GameMode.SPECTATOR);
                    player.teleport(target, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    player.setSpectatorTarget(target);
                    if (player.hasPermission(Permissions.INVENTORY)) {
                        InventoryHandler.swapInventories(player, target);
                    }
                    Spectator.spectators.put(player, target);
                    if (Config.hideFromTab && Spectator.protocolLibPresent) {
                        Spectator.playerHider.hidePlayer(player);
                    }
                    sender.sendMessage(Messages.translate("Messages.Spectate.Other", "player", target.getName()));
                }
                else {
                    sender.sendMessage(Messages.translate("Messages.Player.Offline", "player", args[0]));
                }
                return true;
            }
            else {
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.setGameMode(GameMode.SPECTATOR);
                    if (Config.hideFromTab && Spectator.protocolLibPresent) {
                        Spectator.playerHider.hidePlayer(player);
                    }
                    sender.sendMessage(Messages.translate("Messages.Spectate.General"));
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
            Spectator.spectators.remove(player);
            player.setGameMode(GameMode.SURVIVAL);
            if (player.hasPermission(Permissions.INVENTORY)) {
                InventoryHandler.restoreInventory(player);
            }
            if (Config.hideFromTab && Spectator.protocolLibPresent) {
                Spectator.playerHider.showPlayer(player);
            }
            sender.sendMessage(Messages.translate("Messages.Spectate.Off"));
        }
        else {
            sender.sendMessage(Messages.translate("Messages.Player.NotPlayer"));
        }
        return true;
    }

}
