package com.github.groundbreakingmc.menux.utils;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.platform.PlatformType;
import org.jetbrains.annotations.ApiStatus;

public final class PlatformDetector {

    /**
     * Use {@link MenuxAPI#platformType()} instead
     */
    @ApiStatus.Internal
    public static PlatformType detect() {
        if (classExists("org.bukkit.Bukkit")) return PlatformType.SPIGOT;
        if (classExists("net.fabricmc.api.EnvType")) return PlatformType.FABRIC;
        if (classExists("org.spongepowered.api.Sponge")) return PlatformType.SPONGE;
        if (classExists("com.velocitypowered.api.proxy.ProxyServer")) return PlatformType.VELOCITY;

        throw new IllegalStateException("Unsupported platform");
    }

    private static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private PlatformDetector() {
    }
}
