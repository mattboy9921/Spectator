package com.kosakorner.spectator;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Spectator extends JavaPlugin {

    @Override
    public void onEnable() {
        SpectateCommand commandExecutor = new SpectateCommand();
        PluginCommand command = getCommand("spectate");
        command.setPermission("spectator.use");
        command.setExecutor(commandExecutor);
        command.setTabCompleter(commandExecutor);
    }

}
