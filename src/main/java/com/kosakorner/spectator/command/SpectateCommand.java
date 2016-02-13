package com.kosakorner.spectator.command;

import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                    if (Spectator.spectatorRelations.get(player) == target) {
                        sender.sendMessage(Messages.translate("Messages.Spectate.NoChange", "player", target.getName()));
                        return true;
                    }
                    if (Spectator.spectatorRelations.get(target) == player || target.hasPermission(Permissions.BYPASS_VIEWABLE)) {
                        sender.sendMessage(Messages.translate("Messages.Spectate.NoSpectate", "player", target.getName()));
                        return true;
                    }
                    Spectator.playerHandler.spectatePlayer(player, target);
                    sender.sendMessage(Messages.translate("Messages.Spectate.Other", "player", target.getName()));
                }
                else {
                    sender.sendMessage(Messages.translate("Messages.Player.Offline", "player", args[0]));
                }
                return true;
            }
            else {
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    Spectator.playerHandler.spectatePlayer(player, null);
                    sender.sendMessage(Messages.translate("Messages.Spectate.General"));
                }
                else {
                    Spectator.playerHandler.unspectatePlayer(player);
                    if (Spectator.cycleHandler.isPlayerCycling(player)) {
                        Spectator.cycleHandler.stopCycle(player);
                    }
                    sender.sendMessage(Messages.translate("Messages.Spectate.Off"));
                }
                return true;
            }
        }
        else {
            sender.sendMessage(Messages.translate("Messages.Player.NotPlayer"));
        }
        return true;
    }

}
