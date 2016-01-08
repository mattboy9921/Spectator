package com.kosakorner.spectator.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.BukkitUnwrapper;
import com.comphenix.protocol.reflect.FieldUtils;
import com.comphenix.protocol.reflect.FuzzyReflection;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.*;

public class PacketHandler {

    public PacketHandler(Plugin plugin) {
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

        protocolManager.getAsynchronousManager().registerAsyncHandler(
                new PacketAdapter(plugin, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                            EnumWrappers.EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            if (action.equals(EnumWrappers.EntityUseAction.ATTACK)) {
                                Entity entity = event.getPacket().getEntityModifier(player.getWorld()).read(0);
                                if (entity.getType().equals(EntityType.PLAYER)) {
                                    Player target = (Player) entity;
                                    if (player.hasPermission(Permissions.INVENTORY)) {
                                        InventoryHandler.swapInventories(player, target);
                                    }
                                    Spectator.spectators.put(player, target);
                                }
                            }
                        }
                    }
                }).syncStart();

    }

    public void showPlayer(Player player) {
        sendPlayerPacket(player, true);
    }

    public void hidePlayer(Player player) {
        sendPlayerPacket(player, false);
    }

    private void sendPlayerPacket(Player player, boolean show) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO);
            packet.getPlayerInfoAction().write(0, show ? EnumWrappers.PlayerInfoAction.ADD_PLAYER : EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
            List<PlayerInfoData> infoData = new ArrayList<>();
            infoData.add(new PlayerInfoData(WrappedGameProfile.fromPlayer(player), getPlayerPing(player), EnumWrappers.NativeGameMode.fromBukkit(player.getGameMode()), WrappedChatComponent.fromText(player.getPlayerListName())));
            packet.getPlayerInfoDataLists().write(0, infoData);
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (!target.hasPermission(Permissions.BYPASS_TABLIST)) {
                    protocolManager.sendServerPacket(target, packet);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Field pingField;

    public int getPlayerPing(Player player) throws IllegalAccessException {
        BukkitUnwrapper unwrapper = new BukkitUnwrapper();
        Object entity = unwrapper.unwrapItem(player);
        if (pingField == null) {
            pingField = FuzzyReflection.fromObject(entity).getFieldByName("ping");
        }
        return (Integer) FieldUtils.readField(pingField, entity);
    }

}
