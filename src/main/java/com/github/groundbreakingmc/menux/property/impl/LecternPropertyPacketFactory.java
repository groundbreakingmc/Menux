package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class LecternPropertyPacketFactory implements WindowPropertyPacketFactory {

    private final List<WrapperPlayServerWindowProperty> properties;

    private LecternPropertyPacketFactory(int pageNumber) {
        if (pageNumber != -1) {
            this.properties = ImmutableList.of(new WrapperPlayServerWindowProperty(69, 0, pageNumber));
        } else {
            this.properties = ImmutableList.of();
        }
    }

    @Override
    public @NotNull List<WrapperPlayServerWindowProperty> createPropertyPackets() {
        return this.properties;
    }
}
