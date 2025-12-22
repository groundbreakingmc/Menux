package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BrewingStandPropertyPacketFactory implements WindowPropertyPacketFactory {

    private final List<WrapperPlayServerWindowProperty> properties;

    public BrewingStandPropertyPacketFactory(int brewTime, int fuelTime) {
        final List<WrapperPlayServerWindowProperty> properties = new ArrayList<>(2);
        if (brewTime != -1) properties.add(new WrapperPlayServerWindowProperty(69, 0, brewTime));
        if (fuelTime != -1) properties.add(new WrapperPlayServerWindowProperty(69, 1, fuelTime));
        this.properties = ImmutableList.copyOf(properties);
    }

    @Override
    public @NotNull List<WrapperPlayServerWindowProperty> createPropertyPackets() {
        return this.properties;
    }
}
