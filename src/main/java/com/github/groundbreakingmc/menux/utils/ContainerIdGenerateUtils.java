package com.github.groundbreakingmc.menux.utils;

import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntMaps;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import org.jetbrains.annotations.ApiStatus;

public final class ContainerIdGenerateUtils {

    private static final int MIN_ID = 101;
    private static final int MAX_ID = 200;
    private static final Reference2IntMap<MenuPlayer> IDS = Reference2IntMaps.synchronize(
            new Reference2IntOpenHashMap<>()
    );

    /**
     * Generates the next container (window) ID for the specified player.
     * <p>
     * Container IDs are maintained per player and are incremented cyclically.
     * Vanilla Minecraft cycles container IDs in the range {@code [1..100]}.
     * This implementation uses the range {@code [101..200]} to minimize the
     * likelihood of collisions with vanilla-managed containers.
     * <p>
     * Using a separate range does not provide protocol-level guarantees.
     * Correct behavior relies on validating incoming container-related packets
     * against the currently active container ID for the player.
     * <p>
     * This method is thread-safe and may be invoked from PacketEvents or Netty
     * threads.
     *
     * @param player the player for whom to generate the next container ID
     * @return the next container ID within the configured range
     * @since 1.0.0
     */
    @ApiStatus.Internal
    public static int nextId(MenuPlayer player) {
        final int current = IDS.getOrDefault(player, MIN_ID - 1);
        int next = current + 1;

        if (next > MAX_ID) {
            next = MIN_ID;
        }

        IDS.put(player, next);
        return next;
    }
}
