package com.kosakorner.spectator;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Spectator extends JavaPlugin {

    public static final String PERM_TELEPORT = "spectate.teleport";
    public static final String PERM_INVENTORY = "spectate.inventory";

    public static final Map<Player, ItemStack[]> inventories = new HashMap<>();

    public static boolean showInventories = false;

    @Override
    public void onEnable() {
        SpectateCommand commandExecutor = new SpectateCommand();
        PluginCommand command = getCommand("spectate");
        command.setExecutor(commandExecutor);
        Bukkit.getPluginManager().registerEvents(new PlayerManager(), this);
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            showInventories = true;
            new PacketHandler(this);
        }
    }

}
