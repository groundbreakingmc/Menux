package com.github.groundbreakingmc.menux.menu.builder;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.builder.DefaultButtonBuilder;
import com.github.groundbreakingmc.menux.button.holder.impl.MultipleButtonHolder;
import com.github.groundbreakingmc.menux.button.holder.impl.SimpleButtonHolder;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.menu.MenuType;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.menu.template.impl.DefaultMenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public final class DefaultMenuBuilder {

    private final MenuRegistry menuRegistry;
    private String title = "";
    private MenuType type = MenuType.GENERIC_9x1;
    private List<MenuRule> openRequirements = List.of();
    private List<MenuAction> openActions = List.of();
    private List<MenuAction> closeActions = List.of();
    private ButtonHolder[] buttons = new ButtonHolder[9];
    private Colorizer colorizer = null;
    private PlaceholderParser placeholderParser = null;

    public DefaultMenuBuilder(@NotNull MenuRegistry menuRegistry) {
        this.menuRegistry = menuRegistry;
    }

    public @NotNull MenuRegistry menuRegistry() {
        return this.menuRegistry;
    }

    public @NotNull String title() {
        return this.title;
    }

    public DefaultMenuBuilder title(@NotNull String title) {
        this.title = title;
        return this;
    }

    public @NotNull MenuType type() {
        return this.type;
    }

    public DefaultMenuBuilder type(@NotNull MenuType menuType) {
        this.type = menuType;
        this.buttons = Arrays.copyOf(this.buttons, menuType.size());
        return this;
    }

    public @NotNull List<MenuRule> openRequirements() {
        return this.openRequirements;
    }

    public DefaultMenuBuilder openRequirements(@NotNull List<MenuRule> conditions) {
        this.openRequirements = ImmutableList.copyOf(conditions);
        return this;
    }

    public @NotNull List<MenuAction> openActions() {
        return this.openActions;
    }

    public DefaultMenuBuilder openActions(@NotNull List<MenuAction> actions) {
        this.openActions = ImmutableList.copyOf(actions);
        return this;
    }

    public @NotNull List<MenuAction> closeActions() {
        return this.closeActions;
    }

    public DefaultMenuBuilder closeActions(@NotNull List<MenuAction> actions) {
        this.closeActions = ImmutableList.copyOf(actions);
        return this;
    }

    public @NotNull ButtonHolder[] buttons() {
        return Arrays.copyOf(this.buttons, this.type.size());
    }

    public DefaultMenuBuilder buttons(@Nullable ButtonHolder[] buttons) {
        if (buttons != null) {
            this.buttons = Arrays.copyOf(buttons, buttons.length);
        } else {
            this.buttons = new ButtonHolder[54];
        }
        return this;
    }

    public DefaultMenuBuilder button(int slot, @NotNull ButtonTemplate button) {
        this.buttons[slot] = new SimpleButtonHolder(button);
        return this;
    }

    public DefaultMenuBuilder button(int slot, @NotNull Consumer<DefaultButtonBuilder> buttonFactory) {
        final DefaultButtonBuilder buttonBuilder = new DefaultButtonBuilder();
        buttonFactory.accept(buttonBuilder);
        return this.button(slot, buttonBuilder.build());
    }

    public DefaultMenuBuilder stackButton(int slot, @NotNull ButtonTemplate button) {
        final ButtonHolder old = this.buttons[slot];
        final List<ButtonTemplate> all = old.all();
        if (all.isEmpty()) {
            this.buttons[slot] = new SimpleButtonHolder(button);
        } else {
            final List<ButtonTemplate> list = new ArrayList<>(all);
            list.add(button);
            this.buttons[slot] = new MultipleButtonHolder(list);
        }
        return this;
    }

    public DefaultMenuBuilder stackButton(int slot, @NotNull Consumer<DefaultButtonBuilder> buttonFactory) {
        final DefaultButtonBuilder buttonBuilder = new DefaultButtonBuilder();
        buttonFactory.accept(buttonBuilder);
        return this.stackButton(slot, buttonBuilder.build());
    }

    public DefaultMenuBuilder fillRange(int from, int to, @NotNull ButtonTemplate button) {
        final SimpleButtonHolder holder = new SimpleButtonHolder(button);
        for (int i = from; i <= to; i++) {
            this.buttons[i] = holder;
        }
        return this;
    }

    public Colorizer colorizer() {
        return this.colorizer;
    }

    public DefaultMenuBuilder colorizer(@NotNull Colorizer colorizer) {
        this.colorizer = colorizer;
        return this;
    }

    public PlaceholderParser placeholderParser() {
        return this.placeholderParser;
    }

    public DefaultMenuBuilder placeholderParser(@NotNull PlaceholderParser parser) {
        this.placeholderParser = parser;
        return this;
    }

    public @NotNull MenuTemplate build() {
        return new DefaultMenuTemplate(this);
    }

    // ===== HELPER METHODS =====

    private void stackButton(ButtonTemplate newButton, List<ButtonTemplate> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            final ButtonTemplate button = buttons.get(i);
            if (button.renderPriority() < newButton.renderPriority()) {
                buttons.add(i, button);
                return;
            }
        }
        buttons.add(newButton);
    }
}
