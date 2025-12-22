package com.github.groundbreakingmc.menux.buttons;

import com.github.groundbreakingmc.menux.buttons.builder.DefaultButtonBuilder;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;

@Immutable
public interface ButtonTemplate {

    @NotNull ItemType material();

    int amount();

    int renderPriority();

    @NotNull String displayName();

    @NotNull List<String> lore();

    @NotNull List<MenuRule> viewRequirements();

    @NotNull ClickMap<ClickParams> clickActions();

    default @NotNull ItemStack createItem(@NotNull MenuContext context) {
        final Component displayName = context.menuInst().colorizer().colorizer(
                context.menuInst().placeholderParser().parse(context.player(), this.displayName())
        );

        final List<Component> lore = new ArrayList<>();
        for (final String line : this.lore()) {
            lore.add(context.menuInst().colorizer().colorizer(
                    context.menuInst().placeholderParser().parse(context.player(), line)
            ));
        }

        return ItemStack.builder()
                .type(this.material())
                .amount(this.amount())
                .component(ComponentTypes.ITEM_NAME, displayName)
                .component(ComponentTypes.LORE, new ItemLore(lore))
                .build();
    }

    static DefaultButtonBuilder builder() {
        return new DefaultButtonBuilder();
    }
}
