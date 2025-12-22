package com.github.groundbreakingmc.menux;

import com.github.groundbreakingmc.menux.listener.InventoryListener;
import com.github.groundbreakingmc.menux.manager.PlayerMenuManager;
import com.github.groundbreakingmc.menux.platform.PlatformType;
import com.github.groundbreakingmc.menux.platform.player.factory.MenuPlayerFactory;
import com.github.groundbreakingmc.menux.platform.player.factory.impl.BukkitMenuPlayerFactory;
import com.github.groundbreakingmc.menux.server.ServerAdapter;
import com.github.groundbreakingmc.menux.server.impl.BukkitServerAdapter;
import com.github.groundbreakingmc.menux.utils.PlatformDetector;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import org.jetbrains.annotations.NotNull;

public final class MenuxAPI {

    private static PlatformType platformType;
    private static PlayerMenuManager menuManager;
    private static MenuPlayerFactory playerFactory;
    private static ServerAdapter server;
    private static PacketListenerCommon listener;

    private static boolean initialized;

    public static synchronized void init() {
        final PlatformType platform = PlatformDetector.detect();
        final MenuPlayerFactory playerFactory;
        final ServerAdapter server;
        switch (platform) {
            case SPIGOT -> {
                playerFactory = new BukkitMenuPlayerFactory();
                server = new BukkitServerAdapter();
            }
            default -> throw new IllegalStateException("Unsupported platform");
        }

        init(platform, new PlayerMenuManager(), playerFactory, server);
    }

    /**
     * Package-private init method intended for tests and custom bootstrapping.
     */
    static synchronized void init(PlatformType platformType,
                                  PlayerMenuManager menuManager,
                                  MenuPlayerFactory playerFactory,
                                  ServerAdapter server) {
        if (initialized) {
            throw new IllegalStateException("Already initialized");
        }

        MenuxAPI.platformType = platformType;
        MenuxAPI.menuManager = menuManager;
        MenuxAPI.playerFactory = playerFactory;
        MenuxAPI.server = server;
        listener = PacketEvents.getAPI().getEventManager().registerListener(new InventoryListener());

        initialized = true;
    }

    public static synchronized void terminate() {
        if (!initialized) return;

        menuManager.clearAll();
        PacketEvents.getAPI().getEventManager().unregisterListener(listener);

        platformType = null;
        menuManager = null;
        playerFactory = null;
        server = null;
        listener = null;

        initialized = false;
    }

    @NotNull
    public static PlatformType platformType() {
        checkInit();
        return platformType;
    }

    @NotNull
    public static PlayerMenuManager menuManager() {
        checkInit();
        return menuManager;
    }

    @NotNull
    public static MenuPlayerFactory playerFactory() {
        checkInit();
        return playerFactory;
    }

    @NotNull
    public static ServerAdapter server() {
        checkInit();
        return server;
    }

    private static void checkInit() {
        if (!initialized) {
            throw new IllegalStateException("MenuxAPI is not initialized! Call MenuxAPI#init first.");
        }
    }
}
