package com.github.groundbreakingmc.menux.listener;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;

public final class InventoryListener extends PacketListenerAbstract {

    public InventoryListener() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            final MenuPlayer menuPlayer = MenuxAPI.playerFactory().create(event.getPlayer());
            final var packet = new WrapperPlayClientClickWindow(event);
            event.setCancelled(MenuxAPI.menuManager().handleClick(menuPlayer, packet));
        } else if (event.getPacketType() == PacketType.Play.Client.CLOSE_WINDOW) {
            event.setCancelled(this.handleClose(event.getPlayer(), event.getPacketId(), event.getByteBuf()));
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.OPEN_WINDOW) {
            event.setCancelled(this.handleClose(event.getPlayer(), event.getPacketId(), event.getByteBuf()));
        } else if (event.getPacketType() == PacketType.Play.Server.CLOSE_WINDOW) {
            event.setCancelled(this.handleClose(event.getPlayer(), event.getPacketId(), event.getByteBuf()));
        }
    }

    private boolean handleClose(Object player, int packetId, Object buffer) {
        final MenuPlayer menuPlayer = MenuxAPI.playerFactory().create(player);
        final var packet = new PacketWrapper<>(packetId);
        packet.setBuffer(buffer);
        return MenuxAPI.menuManager().handleClose(menuPlayer, packet.readContainerId());
    }
}
