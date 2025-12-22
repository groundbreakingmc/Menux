package com.github.groundbreakingmc.menux.property.impl;

import com.github.groundbreakingmc.menux.property.WindowPropertyPacketFactory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class EnchantmentTablePropertyPacketFactory implements WindowPropertyPacketFactory {

    private final List<WrapperPlayServerWindowProperty> properties;

    private EnchantmentTablePropertyPacketFactory(Builder builder) {
        final List<WrapperPlayServerWindowProperty> properties = new ArrayList<>(10);
        this.addProperty(0, builder.topLevelRequirement, properties);
        this.addProperty(1, builder.middleLevelRequirement, properties);
        this.addProperty(2, builder.bottomLevelRequirement, properties);
        this.addProperty(3, builder.enchantmentSeed, properties);
        this.addProperty(4, builder.topEnchantmentID, properties);
        this.addProperty(5, builder.middleEnchantmentID, properties);
        this.addProperty(6, builder.bottomEnchantmentID, properties);
        this.addProperty(7, builder.topEnchantmentLevel, properties);
        this.addProperty(8, builder.middleEnchantmentLevel, properties);
        this.addProperty(9, builder.bottomEnchantmentLevel, properties);
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

        private int topLevelRequirement = -1;
        private int middleLevelRequirement = -1;
        private int bottomLevelRequirement = -1;

        private int enchantmentSeed = -1;

        private int topEnchantmentID = -1;
        private int middleEnchantmentID = -1;
        private int bottomEnchantmentID = -1;

        private int topEnchantmentLevel = -1;
        private int middleEnchantmentLevel = -1;
        private int bottomEnchantmentLevel = -1;

        public EnchantmentTablePropertyPacketFactory build() {
            return new EnchantmentTablePropertyPacketFactory(this);
        }
    }
}
