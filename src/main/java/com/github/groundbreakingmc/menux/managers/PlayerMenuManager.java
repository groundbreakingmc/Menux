package com.github.groundbreakingmc.menux.managers;

import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerCloseWindow;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

public final class PlayerMenuManager {

    private final Map<UUID, MenuInstance> openMenus = Reference2ObjectMaps.synchronize(new Reference2ObjectOpenHashMap<>());

    public void open(@NotNull MenuPlayer player, @NotNull MenuTemplate template) {
        this.closeExisting(player);

        final MenuInstance instance = template.createMenu(player);
        final boolean opened = instance.open();

        // MenuInstance сам вызовет registerMenu() если открытие успешно
        if (!opened) {
            // Если меню не открылось (requirements failed), убираем его из кеша
            this.openMenus.remove(player.uuid());
        }
    }

    /**
     * Регистрирует открытое меню. Вызывается из MenuInstance.open()
     * ВАЖНО: Этот метод должен вызываться только из MenuInstance!
     */
    public void registerMenu(@NotNull MenuPlayer player, @NotNull MenuInstance instance) {
        this.openMenus.put(player.uuid(), instance);
    }

    /**
     * Отменяет регистрацию меню. Вызывается когда меню не удалось открыть.
     */
    public void unregisterMenu(@NotNull UUID playerId) {
        this.openMenus.remove(playerId);
    }

    public void closeAll(@NotNull MenuPlayer player) {
        final MenuInstance instance = this.openMenus.remove(player.uuid());
        if (instance != null) {
            player.user().sendPacket(
                    new WrapperPlayServerCloseWindow(instance.containerId())
            );
        }
    }

    @Nullable
    public MenuInstance openMenu(@NotNull UUID playerId) {
        return this.openMenus.get(playerId);
    }

    @Nullable
    public MenuInstance openMenu(@NotNull User user) {
        return this.openMenu(user.getUUID());
    }

    public boolean hasOpenMenu(@NotNull UUID playerId) {
        return this.openMenus.containsKey(playerId);
    }

    public boolean handleClick(@NotNull MenuPlayer player, @NotNull WrapperPlayClientClickWindow packet) {
        final MenuInstance instance = this.validInstance(player.uuid(), packet.getWindowId());
        if (instance == null) {
            return false;
        }

        instance.handleClick(packet);
        return true;
    }

    public boolean handlePlayerClose(@NotNull MenuPlayer player) {
        final MenuInstance instance = this.openMenus.remove(player.uuid());
        if (instance == null) {
            return false;
        }

        instance.handleClose();
        return true;
    }

    public boolean handleClose(@NotNull MenuPlayer player, int windowId) {
        final MenuInstance instance = this.validInstance(player.uuid(), windowId);
        if (instance == null) {
            return false;
        }

        this.openMenus.remove(player.uuid());
        instance.handleClose();
        return true;
    }

    @ApiStatus.Internal
    public void closeAll() {
        this.openMenus.values().forEach(MenuInstance::handleClose);
        this.openMenus.clear();
    }

    public void closeAll(MenuTemplate template) {
        this.openMenus.entrySet().removeIf(entry -> {
            final MenuInstance value = entry.getValue();
            if (value.template() == template) {
                value.handleClose();
                return true;
            }
            return false;
        });
    }

    @Nullable
    private MenuInstance validInstance(@NotNull UUID playerId, int expectedContainerId) {
        final MenuInstance instance = this.openMenus.get(playerId);
        if (instance == null || instance.containerId() != expectedContainerId) {
            return null;
        }
        return instance;
    }

    private void closeExisting(@NotNull MenuPlayer player) {
        final MenuInstance existing = this.openMenus.remove(player.uuid());
        if (existing != null) {
            player.user().sendPacketSilently(
                    new WrapperPlayServerCloseWindow(existing.containerId())
            );
        }
    }
}