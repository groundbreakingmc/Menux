package com.github.groundbreakingmc.menux.button.template.impl;

import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.ClickParams;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.groundbreakingmc.menux.utils.ItemEnchantments;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Map;

@Immutable
public record DefaultButtonTemplate(
        @NotNull ItemType material,
        int amount,
        int damage,
        int renderPriority,
        @Nullable String displayName,
        @Nullable List<String> lore,
        @NotNull ItemEnchantments enchantments,
        @NotNull Map<ComponentType<?>, Object> customComponents,
        @NotNull List<MenuRule> viewRequirements,
        @NotNull ClickMap<ClickParams> clickActions
) implements ButtonTemplate {


    public DefaultButtonTemplate(@NotNull ItemType material,
                                 int amount,
                                 int damage,
                                 int renderPriority,
                                 @Nullable String displayName,
                                 @Nullable List<String> lore,
                                 @NotNull ItemEnchantments enchantments,
                                 @NotNull Map<ComponentType<?>, Object> customComponents,
                                 @NotNull List<MenuRule> viewRequirements,
                                 @NotNull ClickMap<ClickParams> clickActions) {
        if (damage < 0) throw new IllegalArgumentException("Damage can not be less than 0!");
        this.material = material;
        this.amount = amount;
        this.damage = damage;
        this.renderPriority = renderPriority;
        this.displayName = displayName;
        this.lore = lore != null ? ImmutableList.copyOf(lore) : null;
        this.enchantments = enchantments;
        this.customComponents = ImmutableMap.copyOf(customComponents);
        this.viewRequirements = ImmutableList.copyOf(viewRequirements);
        this.clickActions = clickActions;
    }
}
