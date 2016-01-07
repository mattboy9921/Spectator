package com.kosakorner.spectator;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Spectator extends JavaPlugin {

    public static final Map<Player, Player> spectators = new HashMap<>();

    public static final String PERM_TELEPORT = "spectate.teleport";
    public static final String PERM_INVENTORY = "spectate.inventory";

    @Override
    public void onEnable() {
        SpectateCommand commandExecutor = new SpectateCommand();
        PluginCommand command = getCommand("spectate");
        command.setExecutor(commandExecutor);
        Bukkit.getPluginManager().registerEvents(new PlayerHandler(this), this);
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            new PacketHandler(this);
        }
    }

    @Override
    public void onDisable() {
        InventoryManager.restoreAllInventories();
    }

}
