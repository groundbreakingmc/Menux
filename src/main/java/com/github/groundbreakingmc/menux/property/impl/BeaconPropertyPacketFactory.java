package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class BeaconPropertyPacketFactory implements WindowPropertyPacketFactory {

    private final List<WrapperPlayServerWindowProperty> properties;

    private BeaconPropertyPacketFactory(Builder builder) {
        final List<WrapperPlayServerWindowProperty> properties = new ArrayList<>(3);
        this.addProperty(0, builder.powerLevel, properties);
        this.addProperty(1, builder.firstPotionEffect, properties);
        this.addProperty(2, builder.secondPotionEffect, properties);
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

        private int powerLevel = -1;
        private int firstPotionEffect = -1;

        private int secondPotionEffect = -1;

        public Builder setPowerLevel(int powerLevel) {
            this.powerLevel = powerLevel;
            return this;
        }

        public Builder setFirstPotionEffect(int firstPotionEffect) {
            this.firstPotionEffect = firstPotionEffect;
            return this;
        }

        public Builder setSecondPotionEffect(int secondPotionEffect) {
            this.secondPotionEffect = secondPotionEffect;
            return this;
        }

        public BeaconPropertyPacketFactory build() {
            return new BeaconPropertyPacketFactory(this);
        }
    }
}
