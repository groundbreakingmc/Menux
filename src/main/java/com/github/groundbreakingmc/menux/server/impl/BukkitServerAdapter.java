package com.github.groundbreakingmc.menux.server.impl;

import com.github.groundbreakingmc.menux.server.ServerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class BukkitServerAdapter implements ServerAdapter {

    @Override
    public boolean performCommand(@NotNull String command) {
        return Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
    }
}
