package com.github.groundbreakingmc.menux.menu.template.impl;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.managers.PlayerMenuManager;
import com.github.groundbreakingmc.menux.menu.MenuType;
import com.github.groundbreakingmc.menux.menu.builder.DefaultMenuBuilder;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.instance.impl.DefaultMenuInstance;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.groundbreakingmc.menux.utils.ContainerIdGenerateUtils;
import com.google.common.collect.ImmutableList;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Immutable
public final class DefaultMenuTemplate implements MenuTemplate {

    private final MenuRegistry menuRegistry;
    private final String title;
    private final MenuType type;
    private final List<MenuRule> openRequirements;
    private final List<MenuAction> openActions;
    private final List<MenuAction> closeActions;
    private final ButtonHolder[] buttons;
    private final Colorizer colorizer;
    private final PlaceholderParser placeholderParser;

    public DefaultMenuTemplate(DefaultMenuBuilder builder) {
        this.menuRegistry = Objects.requireNonNull(builder.menuRegistry());
        this.title = Objects.requireNonNull(builder.title());
        this.type = Objects.requireNonNull(builder.type());
        this.openRequirements = ImmutableList.copyOf(builder.openRequirements());
        this.openActions = ImmutableList.copyOf(builder.openActions());
        this.closeActions = ImmutableList.copyOf(builder.closeActions());
        this.buttons = Arrays.copyOf(Objects.requireNonNull(builder.buttons()), builder.type().size());
        if (this.buttons.length != this.type.size()) {
            throw new IllegalStateException("Buttons amount is not equals to menu size (Buttons: '" + this.buttons.length
                    + "', Menu Type: '" + this.type.name()
                    + "', Menu Size: '" + this.type.size() + ")"
            );
        }
        this.colorizer = Objects.requireNonNull(builder.colorizer());
        this.placeholderParser = Objects.requireNonNull(builder.placeholderParser());
    }

    @Override
    public @NotNull String title() {
        return this.title;
    }

    @Override
    public @NotNull Component title(@NotNull MenuPlayer player) {
        return this.colorizer.colorizer(
                this.placeholderParser.parse(player, this.title)
        );
    }

    @Override
    public int size() {
        return this.type.size();
    }

    @Override
    public @NotNull MenuType type() {
        return this.type;
    }

    @Override
    public @NotNull List<MenuRule> openRequirements() {
        return this.openRequirements;
    }

    @Override
    public @NotNull List<MenuAction> openActions() {
        return this.openActions;
    }

    @Override
    public @NotNull List<MenuAction> closeActions() {
        return this.closeActions;
    }

    @Override
    public @Nullable ButtonHolder buttonAt(int slot) {
        return this.buttons[slot];
    }

    @Override
    public @Nullable ButtonTemplate buttonAt(int slot, @Nullable MenuContext context) {
        final ButtonHolder holder = this.buttons[slot];
        return holder != null ? holder.button(context) : null;
    }

    @Override
    public @NotNull Colorizer colorizer() {
        return this.colorizer;
    }

    @Override
    public @NotNull PlaceholderParser placeholderParser() {
        return this.placeholderParser;
    }

    /**
     * Use {@link PlayerMenuManager#open(MenuPlayer, MenuTemplate)} instead
     */
    @Override
    @ApiStatus.Internal
    public @NotNull MenuInstance createMenu(@NotNull MenuPlayer player) {
        final int containerId = ContainerIdGenerateUtils.nextId(player);
        return new DefaultMenuInstance(this.menuRegistry, player, this, containerId);
    }
}
