package com.kosakorner.spectator.config;

import com.kosakorner.spectator.Spectator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static boolean mirrorInventory;
    public static boolean hideFromTab;

    public static void loadConfig() {
        File configFile = new File(Spectator.instance.getDataFolder(), "config.yml");
        FileConfiguration config;
        if (!configFile.exists()) {
            config = new YamlConfiguration();
            config.set("Spectator.MirrorInventory", true);
            config.set("Spectator.HideFromTab", false);
            try {
                config.save(configFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        mirrorInventory = config.getBoolean("Spectator.MirrorInventory", true);
        hideFromTab = config.getBoolean("Spectator.HideFromTab", false);
    }

}
