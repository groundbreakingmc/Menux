package com.github.groundbreakingmc.menux.button.builder;

import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.ClickParams;
import com.github.groundbreakingmc.menux.button.template.impl.DefaultButtonTemplate;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.groundbreakingmc.menux.utils.ItemEnchantments;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemDyeColor;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemPotionContents;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemProfile;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.potion.Potion;
import com.github.retrooper.packetevents.protocol.potion.PotionEffect;
import it.unimi.dsi.fastutil.objects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class DefaultButtonBuilder {

    private ItemType material;
    private int amount = 1;
    private int damage = 0;
    private int renderPriority = 0;
    private String displayName;
    private List<String> lore;
    private Object2IntMap<EnchantmentType> enchantments;
    private Map<ComponentType<?>, Object> customComponents;
    private List<MenuRule> viewRequirements;
    private Map<ClickType, ClickParams> clickActions;

    // ==================== Basic Properties ====================

    public DefaultButtonBuilder material(@NotNull ItemType material) {
        this.material = material;
        return this;
    }

    public DefaultButtonBuilder amount(int amount) {
        if (amount < 1) throw new IllegalArgumentException("Amount cannot be less than 1: " + amount);
        this.amount = amount;
        return this;
    }

    public DefaultButtonBuilder damage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("Damage cannot be negative: " + damage);
        this.damage = damage;
        return this;
    }

    public DefaultButtonBuilder renderPriority(int renderPriority) {
        this.renderPriority = renderPriority;
        return this;
    }

    public DefaultButtonBuilder displayName(@Nullable String displayName) {
        this.displayName = displayName;
        return this;
    }

    // ==================== Lore ====================

    public DefaultButtonBuilder lore(@Nullable List<String> lore) {
        this.lore = lore != null ? new ArrayList<>(lore) : null;
        return this;
    }

    public DefaultButtonBuilder addLoreLine(@NotNull String line) {
        if (this.lore == null) this.lore = new ArrayList<>();
        this.lore.add(line);
        return this;
    }

    // ==================== Enchantments ====================

    public DefaultButtonBuilder enchantments(@Nullable Map<EnchantmentType, Integer> enchantments) {
        this.enchantments = enchantments != null ? new Object2IntOpenHashMap<>(enchantments) : null;
        return this;
    }

    public DefaultButtonBuilder addEnchantment(@NotNull EnchantmentType type, int level) {
        if (this.enchantments == null) this.enchantments = new Object2IntOpenHashMap<>();
        this.enchantments.put(type, level);
        return this;
    }

    // ==================== Custom Components ====================

    public DefaultButtonBuilder customComponents(@Nullable Map<ComponentType<?>, Object> customComponents) {
        this.customComponents = customComponents != null ? new Object2ObjectOpenHashMap<>(customComponents) : null;
        return this;
    }

    public <T> DefaultButtonBuilder customComponent(@NotNull ComponentType<T> type, @Nullable T value) {
        if (value == null) {
            if (this.customComponents != null) {
                this.customComponents.remove(type);
            }
            return this;
        }
        if (this.customComponents == null) this.customComponents = new Object2ObjectOpenHashMap<>();
        this.customComponents.put(type, value);
        return this;
    }

    public DefaultButtonBuilder armorColor(int rgb) {
        return customComponent(ComponentTypes.DYED_COLOR, new ItemDyeColor(rgb, true));
    }

    public DefaultButtonBuilder potionColor(@Nullable Potion potion,
                                            @Nullable Integer customColor,
                                            @Nullable List<PotionEffect> customEffects,
                                            @Nullable String customName) {
        return customComponent(ComponentTypes.POTION_CONTENTS,
                new ItemPotionContents(potion, customColor, customEffects != null ? customEffects : List.of(), customName));
    }

    public DefaultButtonBuilder skullOwner(@Nullable String name,
                                           @Nullable UUID id,
                                           @Nullable List<ItemProfile.Property> properties,
                                           @Nullable ItemProfile.SkinPatch skinPatch) {
        return customComponent(ComponentTypes.PROFILE,
                new ItemProfile(name, id, properties != null ? properties : List.of(), skinPatch != null ? skinPatch : ItemProfile.SkinPatch.EMPTY));
    }

    // ==================== View Requirements ====================

    public DefaultButtonBuilder viewRequirements(@Nullable List<MenuRule> requirements) {
        this.viewRequirements = requirements != null ? new ObjectArrayList<>(requirements) : null;
        return this;
    }

    public DefaultButtonBuilder addViewRequirement(@NotNull MenuRule requirement) {
        if (this.viewRequirements == null) this.viewRequirements = new ObjectArrayList<>();
        this.viewRequirements.add(requirement);
        return this;
    }

    // ==================== Click Actions ====================

    public DefaultButtonBuilder clickActions(@Nullable Map<ClickType, ClickParams> clickActions) {
        this.clickActions = clickActions != null ? new HashMap<>(clickActions) : null;
        return this;
    }

    public DefaultButtonBuilder addClickAction(@NotNull ClickType clickType, @NotNull ClickParams clickParams) {
        if (this.clickActions == null) this.clickActions = new HashMap<>();
        this.clickActions.put(clickType, clickParams);
        return this;
    }

    // ==================== Utility ====================

    public DefaultButtonBuilder copy() {
        DefaultButtonBuilder copy = new DefaultButtonBuilder();
        copy.material = this.material;
        copy.amount = this.amount;
        copy.damage = this.damage;
        copy.renderPriority = this.renderPriority;
        copy.displayName = this.displayName;
        copy.lore = this.lore != null ? new ArrayList<>(this.lore) : null;
        copy.enchantments = this.enchantments != null ? new Object2IntOpenHashMap<>(this.enchantments) : null;
        copy.customComponents = this.customComponents != null ? new Object2ObjectOpenHashMap<>(this.customComponents) : null;
        copy.viewRequirements = this.viewRequirements != null ? new ObjectArrayList<>(this.viewRequirements) : null;
        copy.clickActions = this.clickActions != null ? new HashMap<>(this.clickActions) : null;
        return copy;
    }

    // ==================== Build ====================

    public @NotNull ButtonTemplate build() {
        if (this.material == null) {
            throw new IllegalStateException("Material is required to build ButtonTemplate");
        }

        return new DefaultButtonTemplate(
                this.material,
                this.amount,
                this.damage,
                this.renderPriority,
                this.displayName,
                this.lore != null ? List.copyOf(this.lore) : List.of(),
                new ItemEnchantments(this.enchantments != null ? this.enchantments : Object2IntMaps.emptyMap()),
                this.customComponents != null ? Map.copyOf(this.customComponents) : Object2ObjectMaps.emptyMap(),
                this.viewRequirements != null ? List.copyOf(this.viewRequirements) : List.of(),
                new ClickMap<>(this.clickActions != null ? this.clickActions : Object2ObjectMaps.emptyMap())
        );
    }

    // ==================== Static Factory ====================

    public static DefaultButtonBuilder create(@NotNull ItemType material) {
        return new DefaultButtonBuilder().material(material);
    }
}