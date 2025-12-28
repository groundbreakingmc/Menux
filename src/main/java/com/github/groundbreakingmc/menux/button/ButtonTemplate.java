package com.github.groundbreakingmc.menux.button;

import com.github.groundbreakingmc.menux.button.builder.DefaultButtonBuilder;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ClickMap;
import com.github.retrooper.packetevents.protocol.component.ComponentType;
import com.github.retrooper.packetevents.protocol.component.ComponentTypes;
import com.github.retrooper.packetevents.protocol.component.PatchableComponentMap;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemEnchantments;
import com.github.retrooper.packetevents.protocol.component.builtin.item.ItemLore;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Immutable
public interface ButtonTemplate {

    @NotNull ItemType material();

    int amount();

    int damage();

    int renderPriority();

    @Nullable String displayName();

    @Nullable List<String> lore();

    default @NotNull ItemEnchantments enchanments() {
        return ItemEnchantments.EMPTY;
    }

    default @NotNull Map<ComponentType<?>, Object> customComponents() {
        return Map.of();
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
        if (displayName != null && !displayName.isEmpty()) {
            builder.component(ComponentTypes.ITEM_NAME, context.menuInst().colorizer().colorizer(
                    context.menuInst().placeholderParser().parse(context.player(), displayName)
            ));
        }

        final List<String> rawLore = this.lore();
        if (rawLore != null && !rawLore.isEmpty()) {
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

        final Map<ComponentType<?>, Object> customComponents = this.customComponents();
        if (!customComponents.isEmpty()) {
            builder.components(new PatchableComponentMap(customComponents));
        }

        return builder.build();
    }

    static DefaultButtonBuilder builder() {
        return new DefaultButtonBuilder();
    }
}
