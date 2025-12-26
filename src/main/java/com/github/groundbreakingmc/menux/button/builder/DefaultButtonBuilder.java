package com.github.groundbreakingmc.menux.button.builder;

import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.ClickParams;
import com.github.groundbreakingmc.menux.button.template.impl.DefaultButtonTemplate;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.groundbreakingmc.menux.utils.ItemEnchantments;
import com.github.retrooper.packetevents.protocol.item.enchantment.type.EnchantmentType;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DefaultButtonBuilder {

    private ItemType material;
    private int amount = 1;
    private int damage = 0;
    private int renderPriority;
    private String displayName;
    private List<String> lore;
    private Object2IntMap<EnchantmentType> enchantments;
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
        return this.lore;
    }

    public DefaultButtonBuilder lore(List<String> lore) {
        this.lore = lore;
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
                this.viewRequirements,
                new ClickMap<>(this.clickActions)
        );
    }
}
