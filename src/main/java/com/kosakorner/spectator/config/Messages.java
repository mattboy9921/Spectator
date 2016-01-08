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
        try {
            if (!messagesFile.exists()) {
                messages = new YamlConfiguration();
                messages.save(messagesFile);
            }
            messages = YamlConfiguration.loadConfiguration(messagesFile);
            messages.set("Messages.Spectate.General", messages.getString("Messages.Spectate.General", "&bYour are now spectating!"));
            messages.set("Messages.Spectate.Other", messages.getString("Messages.Spectate.Other", "&bYou are now spectating <player>!"));
            messages.set("Messages.Spectate.Off", messages.getString("Messages.Spectate.Off", "&eYou are no longer spectating!"));
            messages.set("Messages.Spectate.Self", messages.getString("Messages.Spectate.Self", "&cYou can't spectate yourself!"));
            messages.set("Messages.Player.NotPlayer", messages.getString("Messages.Player.NotPlayer", "&cYou must be a player to run this command!"));
            messages.set("Messages.Player.Offline", messages.getString("Messages.Player.Offline", "&c<player> isn't online!"));
            messages.set("Messages.Player.GameModeBlocked", messages.getString("Messages.Player.GameModeBlocked", "&cGamemode change blocked, you are currently spectating. Use /spec to leave spectator mode!"));
            messages.set("Messages.Plugin.Reload", messages.getString("Messages.Plugin.Reload", "&bReloaded Spectator configs successfully!"));
            messages.save(messagesFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}