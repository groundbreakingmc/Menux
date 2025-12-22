package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class EmptyPropertyPacketFactory implements WindowPropertyPacketFactory {

    @Override
    public @NotNull List<WrapperPlayServerWindowProperty> createPropertyPackets() {
        return List.of();
    }
}
