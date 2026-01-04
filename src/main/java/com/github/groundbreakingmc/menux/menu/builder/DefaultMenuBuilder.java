package com.github.groundbreakingmc.menux.menu.builder;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.builder.DefaultButtonBuilder;
import com.github.groundbreakingmc.menux.button.holder.impl.MultipleButtonHolder;
import com.github.groundbreakingmc.menux.button.holder.impl.SingleButtonHolder;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.menu.MenuType;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.menu.template.impl.DefaultMenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class DefaultMenuBuilder {

    private final MenuRegistry menuRegistry;
    private String title = "";
    private MenuType type = MenuType.GENERIC_9x1;
    private List<MenuRule> openRequirements = List.of();
    private List<MenuAction> preOpenActions = List.of();
    private List<MenuAction> openActions = List.of();
    private List<MenuAction> preCloseActions = List.of();
    private List<MenuAction> closeActions = List.of();
    private ButtonHolder[] buttons = new ButtonHolder[9];
    private final Map<String, Object> metadata = new Object2ObjectOpenHashMap<>();
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

    public @NotNull List<MenuAction> preOpenActions() {
        return this.preOpenActions;
    }

    public DefaultMenuBuilder preOpenActions(@NotNull List<MenuAction> preOpenActions) {
        this.preOpenActions = ImmutableList.copyOf(preOpenActions);
        return this;
    }

    public @NotNull List<MenuAction> openActions() {
        return this.openActions;
    }

    public DefaultMenuBuilder openActions(@NotNull List<MenuAction> actions) {
        this.openActions = ImmutableList.copyOf(actions);
        return this;
    }

    public @NotNull List<MenuAction> preCloseActions() {
        return this.preCloseActions;
    }

    public DefaultMenuBuilder preCloseActions(@NotNull List<MenuAction> actions) {
        this.preCloseActions = ImmutableList.copyOf(actions);
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
            this.buttons = new ButtonHolder[this.type != null ? this.type.size() : 54];
        }
        return this;
    }

    public DefaultMenuBuilder button(int slot, @Nullable ButtonTemplate button) {
        if (slot < 0 || slot >= this.buttons.length) {
            throw new IllegalArgumentException(
                    "Slot " + slot + " is out of bounds! Valid range: 0-" + (this.buttons.length - 1)
            );
        }

        if (button == null) {
            this.buttons[slot] = null;
            return this;
        }

        final ButtonHolder old = this.buttons[slot];
        final List<ButtonTemplate> all;
        if (old == null || (all = old.all()).isEmpty()) {
            this.buttons[slot] = new SingleButtonHolder(button);
        } else {
            final List<ButtonTemplate> list = new ArrayList<>(all);
            list.add(button);
            this.buttons[slot] = new MultipleButtonHolder(list);
        }
        return this;
    }

    public DefaultMenuBuilder button(int slot, @NotNull Consumer<DefaultButtonBuilder> buttonFactory) {
        final DefaultButtonBuilder buttonBuilder = new DefaultButtonBuilder();
        buttonFactory.accept(buttonBuilder);
        return this.button(slot, buttonBuilder.build());
    }

    public DefaultMenuBuilder fillRange(int from, int to, @NotNull ButtonTemplate button) {
        if (from < 0) {
            throw new IllegalArgumentException("Fill range start cannot be less than 0 (from=" + from + ")!");
        }
        if (to >= this.buttons.length) {
            throw new IllegalArgumentException(
                    "Fill range end cannot be greater than menu size (to=" + to + ", size=" + this.buttons.length + ")!"
            );
        }
        if (from > to) {
            throw new IllegalArgumentException("Fill range start cannot be greater than end (from=" + from + ", to=" + to + ")!");
        }

        final SingleButtonHolder holder = new SingleButtonHolder(button);
        for (int i = from; i <= to; i++) {
            final ButtonHolder old = this.buttons[i];
            final List<ButtonTemplate> all;
            if (old == null || (all = old.all()).isEmpty()) {
                this.buttons[i] = holder;
            } else {
                final List<ButtonTemplate> list = new ArrayList<>(all);
                list.add(button);
                this.buttons[i] = new MultipleButtonHolder(list);
            }
        }
        return this;
    }

    public Map<String, Object> metadata() {
        return Collections.unmodifiableMap(this.metadata);
    }

    public DefaultMenuBuilder metadata(@NotNull String key, @Nullable Object value) {
        if (value == null) {
            this.metadata.remove(key);
        } else {
            this.metadata.put(key, value);
        }
        return this;
    }

    public DefaultMenuBuilder metadata(@Nullable Map<String, Object> metadata) {
        if (metadata == null) {
            this.metadata.clear();
        } else {
            this.metadata.putAll(metadata);
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
}
