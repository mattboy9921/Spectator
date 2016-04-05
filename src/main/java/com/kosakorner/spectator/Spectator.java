package com.kosakorner.spectator;

import com.kosakorner.spectator.command.SpectateCommand;
import com.kosakorner.spectator.command.SpectateCycleCommand;
import com.kosakorner.spectator.command.SpectateReloadCommand;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import com.kosakorner.spectator.cycle.CycleHandler;
import com.kosakorner.spectator.player.InventoryHandler;
import com.kosakorner.spectator.util.PacketListener;
import com.kosakorner.spectator.player.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Spectator extends JavaPlugin {

    public static Spectator instance;

    public static PlayerListener playerListener;
    public static CycleHandler cycleHandler;

    public static final Set<Player> trackedSpectators = new HashSet<>();
    public static final Map<Player, Player> spectatorRelations = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        if (getDataFolder().mkdir()) {
            log("Creating plugin folder!");
        }
        Config.loadConfig();
        Messages.loadMessages();

        PluginCommand command = getCommand("spectate");
        command.setExecutor(new SpectateCommand());
        command = getCommand("spectatecycle");
        command.setExecutor(new SpectateCycleCommand());
        command = getCommand("spectatereload");
        command.setExecutor(new SpectateReloadCommand());

        PacketListener.register();
        playerListener = new PlayerListener();
        cycleHandler = new CycleHandler();
        Bukkit.getPluginManager().registerEvents(playerListener, this);
    }

    @Override
    public void onDisable() {
        InventoryHandler.restoreAllInventories();
        playerListener.restoreAllSpectators();
    }

    public static boolean hasPermission(CommandSender sender, String node) {
        if (sender.hasPermission(Permissions.ALL)) {
            return true;
        }
        if (sender.hasPermission(node)) {
            return true;
        }
        else {
            if (node.contains("use")) {
                return sender.hasPermission(Permissions.USE_ALL);
            }
            if (node.contains("bypass")) {
                return sender.hasPermission(Permissions.BYPASS_ALL);
            }
        }
        return false;
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[Spectator] " + ChatColor.translateAlternateColorCodes('&', message));
    }

}
