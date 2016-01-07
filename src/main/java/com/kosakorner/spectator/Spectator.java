package com.kosakorner.spectator;

import com.kosakorner.spectator.command.SpectateCommand;
import com.kosakorner.spectator.command.SpectateReloadCommand;
import com.kosakorner.spectator.config.Config;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.handler.InventoryHandler;
import com.kosakorner.spectator.handler.PacketHandler;
import com.kosakorner.spectator.handler.PlayerHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Spectator extends JavaPlugin {

    public static Spectator instance;

    public static PacketHandler playerHider;

    public static boolean protocolLibPresent = false;

    public static final Map<Player, Player> spectators = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        getDataFolder().mkdir();
        Config.loadConfig();
        Messages.loadMessages();

        PluginCommand command = getCommand("spectate");
        command.setExecutor(new SpectateCommand());
        command = getCommand("spectatereload");
        command.setExecutor(new SpectateReloadCommand());

        Bukkit.getPluginManager().registerEvents(new PlayerHandler(this), this);
        protocolLibPresent = Bukkit.getPluginManager().isPluginEnabled("ProtocolLib");
        if (protocolLibPresent) {
            playerHider = new PacketHandler(this);
        }
    }

    @Override
    public void onDisable() {
        InventoryHandler.restoreAllInventories();
    }

}
