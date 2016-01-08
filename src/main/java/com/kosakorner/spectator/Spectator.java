package com.kosakorner.spectator;

import com.kosakorner.spectator.command.SpectateCommand;
import com.kosakorner.spectator.command.SpectateReloadCommand;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.handler.InventoryHandler;
import com.kosakorner.spectator.handler.PacketHandler;
import com.kosakorner.spectator.handler.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Spectator extends JavaPlugin {

    public static Spectator instance;

    public static PlayerHandler playerHandler;
    public static PacketHandler packetHandler;

    public static final Set<Player> trackedSpectators = new HashSet<>();
    public static final Map<Player, Player> spectatorRelations = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            log("&4ProtocolLib is not enabled, shutting down. You must install ProtocolLib for this plugin to work!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getDataFolder().mkdir();
        Config.loadConfig();
        Messages.loadMessages();

        PluginCommand command = getCommand("spectate");
        command.setExecutor(new SpectateCommand());
        command = getCommand("spectatereload");
        command.setExecutor(new SpectateReloadCommand());

        playerHandler = new PlayerHandler(this);
        packetHandler = new PacketHandler(this);
        Bukkit.getPluginManager().registerEvents(playerHandler, this);
    }

    @Override
    public void onDisable() {
        InventoryHandler.restoreAllInventories();
    }

    public static void log(String message) {
        Bukkit.getConsoleSender().sendMessage("[Spectator] " + ChatColor.translateAlternateColorCodes('&', message));
    }

}
