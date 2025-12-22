package com.github.groundbreakingmc.menux.menu.context;

import com.github.groundbreakingmc.menux.buttons.ButtonTemplate;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record MenuContext(
        @NotNull MenuPlayer player,
        @NotNull MenuRegistry menuRegistry,
        @NotNull MenuInstance menuInst,
        @Nullable ButtonTemplate clickedButton,
        int clickedSlot,
        @NotNull ClickType clickType
) {

    public MenuContext(@NotNull MenuPlayer player,
                       @NotNull MenuRegistry menuRegistry,
                       @NotNull MenuInstance menuInst) {
        this(player, menuRegistry, menuInst, null, Integer.MIN_VALUE, ClickType.UNKNOWN);
    }
}
