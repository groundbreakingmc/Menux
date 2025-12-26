package com.github.groundbreakingmc.menux.listeners;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import it.unimi.dsi.fastutil.objects.Reference2LongMap;
import it.unimi.dsi.fastutil.objects.Reference2LongMaps;
import it.unimi.dsi.fastutil.objects.Reference2LongOpenHashMap;

public final class InventoryListener extends PacketListenerAbstract {

    // TODO: add clear task to prevent memory leak
    private final Reference2LongMap<Object> lastClickCache = Reference2LongMaps.synchronize(new Reference2LongOpenHashMap<>());

    public InventoryListener() {
        super(PacketListenerPriority.NORMAL);
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() == PacketType.Play.Client.CLICK_WINDOW) {
            final long currentTime = System.currentTimeMillis();
            final long lastClickTime = this.lastClickCache.getLong(event.getPlayer());
            if (lastClickTime != -1 && lastClickTime - currentTime >= 500) {
                return; // prevent netty overloading
            }
            final MenuPlayer menuPlayer = MenuxAPI.playerFactory().create(event.getPlayer());
            final var packet = new WrapperPlayClientClickWindow(event);
            final boolean shouldCancel = MenuxAPI.menuManager().handleClick(menuPlayer, packet);
            if (shouldCancel) {
                this.lastClickCache.put(event.getPlayer(), currentTime);
                event.setCancelled(true);
            }
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
