package com.github.groundbreakingmc.menux.colorizer.impl;

import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public final class MiniMessageColorizer implements Colorizer {

    @Override
    public @NotNull Component colorizer(@NotNull String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }
}
