package com.github.groundbreakingmc.menux.platform.player;

import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface MenuPlayer extends net.kyori.adventure.audience.Audience {

    UUID uuid();

    boolean hasPermission(String permission);

    ItemStack itemAt(int slot);

    boolean performCommand(@NotNull String command);

    @NotNull Object nativePlayer();

    @NotNull User user();
}
