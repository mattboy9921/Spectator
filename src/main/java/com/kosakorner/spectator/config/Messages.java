package com.kosakorner.spectator.config;

import com.kosakorner.spectator.Spectator;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Messages {

    private static FileConfiguration messages;

    public static String translate(String key, Object... replaceList) {
        String message = messages.getString(key, key);
        for (int i = 0; i < replaceList.length; i++) {
            String target = (String) replaceList[i];
            String toInsert = (String) replaceList[i += 1];
            message = message.replace("<" + target + ">", toInsert);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void loadMessages() {
        File messagesFile = new File(Spectator.instance.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messages = new YamlConfiguration();
            messages.set("Messages.Spectate.General", "&bYour are now spectating!");
            messages.set("Messages.Spectate.Other", "&bYou are now spectating <player>!");
            messages.set("Messages.Spectate.Off", "&eYou are no longer spectating!");
            messages.set("Messages.Spectate.Self", "&cYou can't spectate yourself!");
            messages.set("Messages.Player.NotPlayer", "&cYou must be a player to run this command!");
            messages.set("Messages.Player.Offline", "&c<player> isn't online!");
            messages.set("Messages.Plugin.Reload", "&bReloaded Spectator configs successfully!");
            try {
                messages.save(messagesFile);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

}
