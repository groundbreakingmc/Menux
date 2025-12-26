package com.github.groundbreakingmc.menux.platform.player;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface MenuPlayer extends net.kyori.adventure.audience.Audience {

    UUID uuid();

    boolean hasPermission(String permission);

    /**
     * actionbar slots: 0-8
     * inventory slots: 9-35
     * armor slots: 36-39
     * offhand slots: 45
     */
    ItemStack itemAt(int slot);

    boolean performCommand(@NotNull String command);

    @NotNull Object nativePlayer();

    @NotNull User user();
}
