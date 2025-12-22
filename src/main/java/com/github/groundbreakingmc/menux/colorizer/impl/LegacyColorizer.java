package com.github.groundbreakingmc.menux.colorizer.impl;

import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.jetbrains.annotations.NotNull;

public final class LegacyColorizer implements Colorizer {
    @Override
    public @NotNull Component colorizer(@NotNull String text) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(text);
    }
}
