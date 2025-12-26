package com.github.groundbreakingmc.menux.menu.template;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.menu.MenuType;
import com.github.groundbreakingmc.menux.menu.builder.DefaultMenuBuilder;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.Immutable;
import java.util.List;

@Immutable
public interface MenuTemplate {

    @NotNull String title();

    @NotNull Component title(@NotNull MenuPlayer player);

    int size();

    @NotNull MenuType type();

    @NotNull List<MenuRule> openRequirements();

    @NotNull List<MenuAction> openActions();

    @NotNull List<MenuAction> closeActions();

    @Nullable ButtonHolder buttonAt(int slot);

    @Nullable ButtonTemplate buttonAt(int slot, @Nullable MenuContext context);

    @NotNull Colorizer colorizer();

    @NotNull PlaceholderParser placeholderParser();

    @NotNull MenuInstance createMenu(@NotNull MenuPlayer player);

    static @NotNull DefaultMenuBuilder builder(@NotNull MenuRegistry menuRegistry) {
        return new DefaultMenuBuilder(menuRegistry);
    }
}
