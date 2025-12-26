package com.github.groundbreakingmc.menux.button;

import com.github.groundbreakingmc.menux.button.builder.DefaultButtonBuilder;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments;
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

    int damage();

    int renderPriority();

    @NotNull String displayName();

    @NotNull List<String> lore();

    default @NotNull ItemEnchantments enchanments() {
        return ItemEnchantments.EMPTY;
    }

    @NotNull List<MenuRule> viewRequirements();

    @NotNull ClickMap<ClickParams> clickActions();

    default @NotNull ItemStack createItem(@NotNull MenuContext context) {
        final ItemStack.Builder builder = ItemStack.builder()
                .type(this.material())
                .amount(this.amount());

        final int damage = this.damage();
        if (damage > 0) {
            builder.component(ComponentTypes.DAMAGE, damage);
        }

        final String displayName = this.displayName();
        if (!displayName.isEmpty()) {
            builder.component(ComponentTypes.ITEM_NAME, context.menuInst().colorizer().colorizer(
                    context.menuInst().placeholderParser().parse(context.player(), displayName)
            ));
        }

        final List<String> rawLore = this.lore();
        if (!rawLore.isEmpty()) {
            final List<Component> lore = new ArrayList<>();
            for (final String line : rawLore) {
                lore.add(context.menuInst().colorizer().colorizer(
                        context.menuInst().placeholderParser().parse(context.player(), line)
                ));
            }
            builder.component(ComponentTypes.LORE, new ItemLore(lore));
        }

        final ItemEnchantments enchanments = this.enchanments();
        if (enchanments != ItemEnchantments.EMPTY) {
            builder.component(ComponentTypes.ENCHANTMENTS, enchanments);
        }

        return builder.build();
    }

    static DefaultButtonBuilder builder() {
        return new DefaultButtonBuilder();
    }
}
