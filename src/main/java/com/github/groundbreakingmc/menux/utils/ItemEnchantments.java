package com.github.groundbreakingmc.menux.utils;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentTypes;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

public class ItemEnchantments implements Iterable<Object2IntMap.Entry<EnchantmentType>> {

    public static final ItemEnchantments EMPTY = new ItemEnchantments(Object2IntMaps.emptyMap(), true) {
        @Override
        public void enchantments(Object2IntMap<EnchantmentType> enchantments) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void showInTooltip(boolean showInTooltip) {
            throw new UnsupportedOperationException();
        }
    };

    private Object2IntMap<EnchantmentType> enchantments;

    /**
     * Removed in 1.21.5
     */
    @ApiStatus.Obsolete
    private boolean showInTooltip;

    public ItemEnchantments(Object2IntMap<EnchantmentType> enchantments) {
        this(enchantments, true);
    }

    /**
     * Removed in 1.21.5
     */
    @ApiStatus.Obsolete
    public ItemEnchantments(Object2IntMap<EnchantmentType> enchantments, boolean showInTooltip) {
        this.enchantments = new Object2IntOpenHashMap<>(enchantments);
        this.showInTooltip = showInTooltip;
    }

    public static ItemEnchantments read(PacketWrapper<?> wrapper) {
        final Object2IntMap<EnchantmentType> enchantments = readMap(wrapper);
        final boolean showInTooltip = wrapper.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_21_5) || wrapper.readBoolean();
        return new ItemEnchantments(enchantments, showInTooltip);
    }

    public static void write(PacketWrapper<?> wrapper, ItemEnchantments enchantments) {
        final ClientVersion version = wrapper.getServerVersion().toClientVersion();
        writeMap(wrapper, enchantments.enchantments(), version);
        if (wrapper.getServerVersion().isOlderThan(ServerVersion.V_1_21_5)) {
            wrapper.writeBoolean(enchantments.showInTooltip());
        }
    }

    public Object2IntMap<EnchantmentType> enchantments() {
        return Object2IntMaps.unmodifiable(this.enchantments);
    }

    public void enchantments(Object2IntMap<EnchantmentType> enchantments) {
        this.enchantments = new Object2IntOpenHashMap<>(enchantments);
    }

    public int enchantmentLevel(EnchantmentType enchantment) {
        return this.enchantments.getOrDefault(enchantment, 0);
    }

    public void enchantmentLevel(EnchantmentType enchantment, int level) {
        if (level <= 0) {
            this.enchantments.removeInt(enchantment);
        } else {
            this.enchantments.put(enchantment, level);
        }
    }

    public boolean isEmpty() {
        return this.enchantmentCount() < 1;
    }

    public int enchantmentCount() {
        return this.enchantments.size();
    }

    /**
     * Removed in 1.21.5
     */
    @ApiStatus.Obsolete
    public boolean showInTooltip() {
        return this.showInTooltip;
    }

    /**
     * Removed in 1.21.5
     */
    @ApiStatus.Obsolete
    public void showInTooltip(boolean showInTooltip) {
        this.showInTooltip = showInTooltip;
    }

    @Override
    public @NotNull Iterator<Object2IntMap.Entry<EnchantmentType>> iterator() {
        return this.enchantments.object2IntEntrySet().iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemEnchantments that)) return false;
        if (this.showInTooltip != that.showInTooltip) return false;
        return this.enchantments.equals(that.enchantments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.enchantments, this.showInTooltip);
    }

    @Override
    public String toString() {
        return "ItemEnchantments{enchantments=" + this.enchantments + ", showInTooltip=" + this.showInTooltip + '}';
    }

    // ===== Utility Methods =====


    private static Object2IntMap<EnchantmentType> readMap(PacketWrapper<?> wrapper) {
        final int size = wrapper.readVarInt();
        final Object2IntMap<EnchantmentType> map = new Object2IntOpenHashMap<>(size);
        for (int i = 0; i < size; i++) {
            final EnchantmentType key = wrapper.readMappedEntity(EnchantmentTypes.getRegistry());
            final int value = wrapper.readVarInt();
            map.put(key, value);
        }
        return map;
    }

    private static void writeMap(PacketWrapper<?> wrapper, Object2IntMap<EnchantmentType> enchantments, ClientVersion clientVersion) {
        wrapper.writeVarInt(enchantments.size());
        for (final var entry : enchantments.object2IntEntrySet()) {
            final EnchantmentType enchantment = entry.getKey();
            final int level = entry.getIntValue();
            wrapper.writeInt(enchantment.getId(clientVersion));
            wrapper.writeVarInt(level);
        }
    }
}
