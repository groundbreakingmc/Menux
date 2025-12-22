package com.github.groundbreakingmc.menux.platform.player.factory;

import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import org.jetbrains.annotations.NotNull;

public interface MenuPlayerFactory {
    @NotNull MenuPlayer create(@NotNull Object player);
}
