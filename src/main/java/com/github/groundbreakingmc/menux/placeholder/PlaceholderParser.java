package com.github.groundbreakingmc.menux.placeholder;

import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import org.jetbrains.annotations.NotNull;

public interface PlaceholderParser {

    @NotNull String parse(@NotNull MenuPlayer player, @NotNull String text);
}
