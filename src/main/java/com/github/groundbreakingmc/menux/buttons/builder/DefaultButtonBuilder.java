package com.github.groundbreakingmc.menux.buttons.builder;

import com.github.groundbreakingmc.menux.buttons.ButtonTemplate;
import com.github.groundbreakingmc.menux.buttons.ClickParams;
import com.github.groundbreakingmc.menux.buttons.template.impl.DefaultButtonTemplate;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class DefaultButtonBuilder {

    private ItemType material;
    private int amount;
    private int renderPriority;
    private String displayName;
    private List<String> lore;
    private List<MenuRule> viewRequirements;
    private ClickMap<ClickParams> clickActions;

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

    public DefaultButtonBuilder viewRequirements(List<MenuRule> viewRequirements) {
        this.viewRequirements = viewRequirements;
        return this;
    }

    public ClickMap<ClickParams> clickActions() {
        return this.clickActions;
    }

    public DefaultButtonBuilder clickActions(ClickMap<ClickParams> clickActions) {
        this.clickActions = clickActions;
        return this;
    }

    public @NotNull ButtonTemplate build() {
        return new DefaultButtonTemplate(
                this.material,
                this.amount,
                this.renderPriority,
                this.displayName,
                this.lore,
                this.viewRequirements,
                this.clickActions
        );
    }
}
