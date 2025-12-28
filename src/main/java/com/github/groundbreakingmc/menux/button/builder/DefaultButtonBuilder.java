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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class DefaultButtonBuilder {

    private ItemType material;
    private int amount = 1;
    private int damage = 0;
    private int renderPriority;
    private String displayName;
    private List<String> lore;
    private Object2IntMap<EnchantmentType> enchantments;
    private Map<ComponentType<?>, Object> customComponents;
    private List<MenuRule> viewRequirements;
    private Map<ClickType, ClickParams> clickActions;

    public ItemType material() {
        return this.material;
    }

    public DefaultButtonBuilder material(ItemType material) {
        this.material = material;
        return this;
    }

    public int amount() {
        return this.amount;
    }

    public DefaultButtonBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public int damage() {
        return this.damage;
    }

    public DefaultButtonBuilder damage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("Damage can not be less than 0 (Got: " + damage + ")!");
        this.damage = damage;
        return this;
    }

    public int renderPriority() {
        return this.renderPriority;
    }

    public DefaultButtonBuilder renderPriority(int renderPriority) {
        this.renderPriority = renderPriority;
        return this;
    }

    public String displayName() {
        return this.displayName;
    }

    public DefaultButtonBuilder displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public List<String> lore() {
        return Collections.unmodifiableList(this.lore);
    }

    public DefaultButtonBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public Object2IntMap<EnchantmentType> enchantments() {
        return Object2IntMaps.unmodifiable(this.enchantments);
    }

    public DefaultButtonBuilder enchantments(@NotNull EnchantmentType type, int level) {
        if (this.enchantments == null) this.enchantments = new Object2IntOpenHashMap<>();
        this.enchantments.put(type, level);
        return this;
    }

    public DefaultButtonBuilder enchantments(Object2IntMap<EnchantmentType> enchantments) {
        this.enchantments = enchantments;
        return this;
    }

    public Map<ComponentType<?>, Object> customComponents() {
        return Collections.unmodifiableMap(this.customComponents);
    }

    public DefaultButtonBuilder armorColor(int rgb) {
        return customComponent(ComponentTypes.DYED_COLOR,
                new ItemDyeColor(rgb, true));
    }

    public DefaultButtonBuilder potionColor(@Nullable Potion potion,
                                            @Nullable Integer customColor,
                                            List<PotionEffect> customEffects,
                                            @Nullable String customName) {
        return customComponent(ComponentTypes.POTION_CONTENTS,
                new ItemPotionContents(potion, customColor, customEffects, customName));
    }

    public DefaultButtonBuilder customModelData(int data) {
        return customComponent(ComponentTypes.CUSTOM_MODEL_DATA, data);
    }

    public DefaultButtonBuilder skullOwner(@Nullable String name,
                                           @Nullable UUID id,
                                           List<ItemProfile.Property> properties,
                                           @Nullable ItemProfile.SkinPatch skinPatch) {
        return customComponent(ComponentTypes.PROFILE,
                new ItemProfile(name, id, properties, skinPatch != null ? skinPatch : ItemProfile.SkinPatch.EMPTY));
    }

    public <T> DefaultButtonBuilder customComponent(ComponentType<T> type, T value) {
        if (this.customComponents == null) {
            this.customComponents = new Object2ObjectOpenHashMap<>();
        }
        this.customComponents.put(type, value);
        return this;
    }

    public <T> DefaultButtonBuilder customComponent(@Nullable Map<ComponentType<?>, Object> customComponents) {
        if (customComponents == null) {
            this.customComponents = Map.of();
        } else {
            if (this.customComponents == null) this.customComponents = new Object2ObjectOpenHashMap<>();
            this.customComponents.clear();
            this.customComponents.putAll(customComponents);
        }
        return this;
    }

    public List<MenuRule> viewRequirements() {
        return this.viewRequirements;
    }

    public DefaultButtonBuilder viewRequirements(MenuRule requirement) {
        if (this.viewRequirements == null) this.viewRequirements = new ArrayList<>();
        this.viewRequirements.add(requirement);
        return this;
    }

    public DefaultButtonBuilder viewRequirements(List<MenuRule> requirements) {
        this.viewRequirements = requirements;
        return this;
    }

    public Map<ClickType, ClickParams> clickActions() {
        return this.clickActions;
    }

    public DefaultButtonBuilder clickActions(@NotNull ClickType clickType, @NotNull ClickParams clickParams) {
        if (this.clickActions == null) this.clickActions = new HashMap<>();
        this.clickActions.put(clickType, clickParams);
        return this;
    }

    public DefaultButtonBuilder clickActions(@NotNull Map<ClickType, ClickParams> clickActions) {
        this.clickActions = clickActions;
        return this;
    }

    public @NotNull ButtonTemplate build() {
        if (this.enchantments == null) this.enchantments = Object2IntMaps.emptyMap();
        return new DefaultButtonTemplate(
                this.material,
                this.amount,
                this.damage,
                this.renderPriority,
                this.displayName,
                this.lore,
                new ItemEnchantments(this.enchantments),
                customComponents,
                this.viewRequirements,
                new ClickMap<>(this.clickActions)
        );
    }
}
