package com.kosakorner.spectator.config;

import com.kosakorner.spectator.Spectator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    public static boolean mirrorInventory;
    public static boolean hideFromTab;
    public static boolean cycleOnPlayerDeath;
    public static boolean rememberSurvivalPosition;

    public static void loadConfig() {
        File configFile = new File(Spectator.instance.getDataFolder(), "config.yml");
        FileConfiguration config;
        try {
            if (!configFile.exists()) {
                config = new YamlConfiguration();
                config.save(configFile);
            }
            config = YamlConfiguration.loadConfiguration(configFile);
            config.set("Spectator.MirrorInventory", mirrorInventory = config.getBoolean("Spectator.MirrorInventory", true));
            config.set("Spectator.HideFromTab", hideFromTab = config.getBoolean("Spectator.HideFromTab", false));
            config.set("Spectator.CycleOnPlayerDeath", cycleOnPlayerDeath = config.getBoolean("Spectator.CycleOnPlayerDeath", false));
            config.set("Spectator.RememberSurvivalPosition", rememberSurvivalPosition = config.getBoolean("Spectator.RememberSurvivalPosition", true));
            config.save(configFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
