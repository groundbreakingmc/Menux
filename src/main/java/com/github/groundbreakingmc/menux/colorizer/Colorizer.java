package com.github.groundbreakingmc.menux.colorizer;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface Colorizer {

    @NotNull Component colorizer(@NotNull String text);
}
