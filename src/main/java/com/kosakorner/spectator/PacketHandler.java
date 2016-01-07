package com.kosakorner.spectator;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PacketHandler {

    @SuppressWarnings("deprecation")
    public PacketHandler(Plugin plugin) {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

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
                                    if (player.hasPermission(Spectator.PERM_INVENTORY)) {
                                        Spectator.inventories.put(player, player.getInventory().getContents());
                                        player.getInventory().clear();
                                        player.getInventory().setContents(target.getInventory().getContents());
                                        player.updateInventory();
                                    }
                                }
                            }
                        }
                    }
                }).syncStart();
    }

}
