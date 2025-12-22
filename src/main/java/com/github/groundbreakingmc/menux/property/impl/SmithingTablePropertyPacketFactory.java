package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class SmithingTablePropertyPacketFactory implements WindowPropertyPacketFactory {

    private final List<WrapperPlayServerWindowProperty> properties;

    private SmithingTablePropertyPacketFactory(int hasRecipeError) {
        if (hasRecipeError != -1) {
            this.properties = ImmutableList.of(new WrapperPlayServerWindowProperty(69, 0, hasRecipeError));
        } else {
            this.properties = ImmutableList.of();
        }
    }

    @Override
    public @NotNull List<WrapperPlayServerWindowProperty> createPropertyPackets() {
        return this.properties;
    }
}
