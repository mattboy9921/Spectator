package com.kosakorner.spectator;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectator extends JavaPlugin {

    public static final String PERM_TELEPORT = "spectate.teleport";

    @Override
    public void onEnable() {
        SpectateCommand commandExecutor = new SpectateCommand();
        PluginCommand command = getCommand("spectate");
        command.setExecutor(commandExecutor);
    }

}
