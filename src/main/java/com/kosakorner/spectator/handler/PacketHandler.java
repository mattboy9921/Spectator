package com.kosakorner.spectator.handler;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.kosakorner.spectator.Spectator;
import com.kosakorner.spectator.config.Messages;
import com.kosakorner.spectator.config.Permissions;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PacketHandler {

    public PacketHandler() {
        final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.getAsynchronousManager().registerAsyncHandler(
                new PacketAdapter(Spectator.instance, PacketType.Play.Client.USE_ENTITY) {
                    @Override
                    public void onPacketReceiving(PacketEvent event) {
                        Player player = event.getPlayer();
                        if (player.getGameMode().equals(GameMode.SPECTATOR)) {
                            EnumWrappers.EntityUseAction action = event.getPacket().getEntityUseActions().read(0);
                            if (action.equals(EnumWrappers.EntityUseAction.ATTACK)) {
                                Entity entity = event.getPacket().getEntityModifier(player.getWorld()).read(0);
                                if (entity.getType().equals(EntityType.PLAYER)) {
                                    Player target = (Player) entity;
                                    if (Spectator.hasPermission(target, Permissions.BYPASS_VIEWABLE)) {
                                        player.sendMessage(Messages.translate("Messages.Spectate.NoSpectate", "player", target.getName()));
                                        event.setCancelled(true);
                                    }
                                    else {
                                        if (Spectator.hasPermission(player, Permissions.INVENTORY)) {
                                            InventoryHandler.swapInventories(player, target);
                                        }
                                        Spectator.spectatorRelations.remove(player);
                                        Spectator.spectatorRelations.put(player, target);
                                    }
                                }
                            }
                        }
                    }
                }).syncStart();

    }

}
