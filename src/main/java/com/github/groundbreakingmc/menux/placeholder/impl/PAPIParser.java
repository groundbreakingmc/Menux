package com.github.groundbreakingmc.menux.placeholder.impl;

import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class PAPIParser implements PlaceholderParser {

    @Override
    public @NotNull String parse(@NotNull MenuPlayer player, @NotNull String text) {
        return PlaceholderAPI.setPlaceholders((Player) player.nativePlayer(), text);
    }

//    @Override
//    public @Nullable String parse(@NotNull MenuPlayer player, @NotNull String identifier, @NotNull String params) {
//        final PlaceholderExpansion expansion = PlaceholderAPIPlugin.getInstance().getLocalExpansionManager().getExpansion(identifier);
//        if (expansion != null) return expansion.onRequest((Player) player.nativePlayer(), params);
//        return identifier;
//    }
}
