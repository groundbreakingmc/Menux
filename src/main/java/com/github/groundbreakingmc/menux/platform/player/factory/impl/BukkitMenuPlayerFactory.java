package com.github.groundbreakingmc.menux.platform.player.factory.impl;

import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.platform.player.factory.MenuPlayerFactory;
import com.github.groundbreakingmc.menux.platform.player.impl.BukkitMenuPlayer;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

public final class BukkitMenuPlayerFactory implements MenuPlayerFactory {

    private final Map<UUID, MenuPlayer> cache = new Reference2ObjectOpenHashMap<>();

    @Override
    public @NotNull MenuPlayer create(@NotNull Object player) {
        final Player bukkitPlayer = (Player) player;
        return this.cache.computeIfAbsent(bukkitPlayer.getUniqueId(), uuid ->
                new BukkitMenuPlayer(bukkitPlayer, bukkitPlayer)
        );
    }
}
