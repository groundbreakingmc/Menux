package com.github.groundbreakingmc.menux.managers;

import com.github.groundbreakingmc.menux.menu.processor.MenuProcessor;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
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

    private final Map<UUID, MenuProcessor> openMenus = Reference2ObjectMaps.synchronize(new Reference2ObjectOpenHashMap<>());

    public void open(@NotNull MenuPlayer player, @NotNull MenuTemplate template, @NotNull MenuRegistry menuRegistry) {
        this.closeExisting(player);

        final MenuProcessor menu = new MenuProcessor(menuRegistry, player, template);
        final boolean opened = menu.open();

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
    public void registerMenu(@NotNull MenuPlayer player, @NotNull MenuProcessor processor) {
        this.openMenus.put(player.uuid(), processor);
    }

    /**
     * Отменяет регистрацию меню. Вызывается когда меню не удалось открыть.
     */
    public void unregisterMenu(@NotNull UUID playerId) {
        this.openMenus.remove(playerId);
    }

    public void closeAll(@NotNull MenuPlayer player) {
        final MenuProcessor processor = this.openMenus.remove(player.uuid());
        if (processor != null) {
            player.user().sendPacket(
                    new WrapperPlayServerCloseWindow(processor.containerId())
            );
        }
    }

    @Nullable
    public MenuProcessor openMenu(@NotNull UUID playerId) {
        return this.openMenus.get(playerId);
    }

    @Nullable
    public MenuProcessor openMenu(@NotNull User user) {
        return this.openMenu(user.getUUID());
    }

    public boolean hasOpenMenu(@NotNull UUID playerId) {
        return this.openMenus.containsKey(playerId);
    }

    public boolean handleClick(@NotNull MenuPlayer player, @NotNull WrapperPlayClientClickWindow packet) {
        final MenuProcessor processor = this.validInstance(player.uuid(), packet.getWindowId());
        if (processor == null) {
            return false;
        }

        processor.handleClick(packet);
        return true;
    }

    public boolean handlePlayerClose(@NotNull MenuPlayer player) {
        final MenuProcessor processor = this.openMenus.remove(player.uuid());
        if (processor == null) {
            return false;
        }

        processor.handleClose();
        return true;
    }

    public boolean handleClose(@NotNull MenuPlayer player, int windowId) {
        final MenuProcessor processor = this.validInstance(player.uuid(), windowId);
        if (processor == null) {
            return false;
        }

        this.openMenus.remove(player.uuid());
        processor.handleClose();
        return true;
    }

    @ApiStatus.Internal
    public void closeAll() {
        this.openMenus.values().forEach(MenuProcessor::handleClose);
        this.openMenus.clear();
    }

    public void closeAll(MenuTemplate template) {
        this.openMenus.entrySet().removeIf(entry -> {
            final MenuProcessor value = entry.getValue();
            if (value.template() == template) {
                value.handleClose();
                return true;
            }
            return false;
        });
    }

    @Nullable
    private MenuProcessor validInstance(@NotNull UUID playerId, int expectedContainerId) {
        final MenuProcessor processor = this.openMenus.get(playerId);
        if (processor == null || processor.containerId() != expectedContainerId) {
            return null;
        }
        return processor;
    }

    private void closeExisting(@NotNull MenuPlayer player) {
        final MenuProcessor existing = this.openMenus.remove(player.uuid());
        if (existing != null) {
            player.user().sendPacketSilently(
                    new WrapperPlayServerCloseWindow(existing.containerId())
            );
        }
    }
}