package com.github.groundbreakingmc.menux.buttons.template.impl;

import com.github.groundbreakingmc.menux.buttons.ButtonTemplate;
import com.github.groundbreakingmc.menux.buttons.ClickParams;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public record DefaultButtonTemplate(
        @NotNull ItemType material,
        int amount,
        int renderPriority,
        @NotNull String displayName,
        @NotNull List<String> lore,
        @NotNull List<MenuRule> viewRequirements,
        @NotNull ClickMap<ClickParams> clickActions
) implements ButtonTemplate {

    public DefaultButtonTemplate(@NotNull ItemType material,
                                 int amount,
                                 int renderPriority,
                                 @NotNull String displayName,
                                 @NotNull List<String> lore,
                                 @NotNull List<MenuRule> viewRequirements,
                                 @NotNull ClickMap<ClickParams> clickActions) {
        this.material = material;
        this.amount = amount;
        this.renderPriority = renderPriority;
        this.displayName = displayName;
        this.lore = ImmutableList.copyOf(lore);
        this.viewRequirements = ImmutableList.copyOf(viewRequirements);
        this.clickActions = clickActions;
    }
}
