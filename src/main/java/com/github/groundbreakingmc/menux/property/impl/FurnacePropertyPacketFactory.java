package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class FurnacePropertyPacketFactory implements WindowPropertyPacketFactory {

    private final List<WrapperPlayServerWindowProperty> properties;

    public FurnacePropertyPacketFactory(Builder builder) {
        final List<WrapperPlayServerWindowProperty> properties = new ArrayList<>(4);
        this.addProperty(0, builder.fuelLeft, properties);
        this.addProperty(1, builder.maxFuelBurnTime, properties);
        this.addProperty(2, builder.progressArrow, properties);
        this.addProperty(3, builder.maximumProgress, properties);
        this.properties = ImmutableList.copyOf(properties);
    }

    @Override
    public @NotNull List<WrapperPlayServerWindowProperty> createPropertyPackets() {
        return this.properties;
    }

    private void addProperty(int id, int property, List<WrapperPlayServerWindowProperty> properties) {
        if (property != -1) properties.add(new WrapperPlayServerWindowProperty(69, id, property));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int fuelLeft = -1;
        private int maxFuelBurnTime = -1;
        private int progressArrow = -1;
        private int maximumProgress = -1;

        public Builder setFuelLeft(int fuelLeft) {
            this.fuelLeft = fuelLeft;
            return this;
        }

        public Builder setMaxFuelBurnTime(int maxFuelBurnTime) {
            this.maxFuelBurnTime = maxFuelBurnTime;
            return this;
        }

        public Builder setProgressArrow(int progressArrow) {
            this.progressArrow = progressArrow;
            return this;
        }

        public Builder setMaximumProgress(int maximumProgress) {
            this.maximumProgress = maximumProgress;
            return this;
        }

        public FurnacePropertyPacketFactory build() {
            return new FurnacePropertyPacketFactory(this);
        }
    }
}
