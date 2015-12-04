package com.kosakorner.spectator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpectateCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    toggleGameMode(player);
                    player.teleport(target);
                    sender.sendMessage(ChatColor.AQUA + "You are now spectating " + target.getName() + "!");
                }
                else {
                    sender.sendMessage(ChatColor.RED + args[0] + " isn't online!");
                }
            }
            else {
                toggleGameMode(player);
                sender.sendMessage(ChatColor.AQUA + "You are now spectating!");
            }
        }
        else {
            sender.sendMessage(ChatColor.RED + "You must be a player to run this command!");
        }
        return true;
    }

    private void toggleGameMode(Player player) {
        if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
            player.setGameMode(GameMode.SPECTATOR);
        }
        else {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> matches = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : Bukkit.matchPlayer(args[0])) {
                matches.add(player.getName());
            }
            return matches;
        }
        return matches;
    }

}
