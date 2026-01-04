package com.github.groundbreakingmc.menux.action.registry;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.action.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for menu action factories.
 * Stores and provides access to action factories by their type names.
 */
@SuppressWarnings("unused")
public final class MenuActionRegistry {

    private final Map<String, MenuAction.Factory> factories = new ConcurrentHashMap<>();

    /**
     * Registers all default built-in actions.
     */
    public void registerDefaultActions() {
        this.register("close-menu", new CloseMenuAction.Factory())
                .register("run-command", new CommandMenuAction.Factory())
                .register("fill-menu", new FillAction.Factory())
                .register("send-message", new MessageAction.Factory())
                .register("open-menu", new OpenMenuAction.Factory())
                .register("play-sound", new PlaySoundAction.Factory());
    }

    /**
     * Registers a new action factory with the given type name.
     *
     * @param type    the action type identifier (case-insensitive)
     * @param factory the factory instance to register
     * @return this registry for chaining
     */
    public @NotNull MenuActionRegistry register(@NotNull String type, @NotNull MenuAction.Factory factory) {
        this.factories.put(type.toLowerCase(), factory);
        return this;
    }

    /**
     * Retrieves a factory by its type name.
     *
     * @param type the action type identifier (case-insensitive)
     * @return the factory, or null if not found
     */
    public @Nullable MenuAction.Factory get(@NotNull String type) {
        return this.factories.get(type.toLowerCase());
    }

    /**
     * Checks if a factory is registered for the given type.
     *
     * @param type the action type identifier (case-insensitive)
     * @return true if the factory exists
     */
    public boolean has(@NotNull String type) {
        return this.factories.containsKey(type.toLowerCase());
    }

    /**
     * Unregisters a factory by its type name.
     *
     * @param type the action type identifier (case-insensitive)
     * @return the removed factory, or null if it wasn't registered
     */
    public @Nullable MenuAction.Factory unregister(@NotNull String type) {
        return this.factories.remove(type.toLowerCase());
    }

    /**
     * Clears all registered factories.
     */
    public void clear() {
        this.factories.clear();
    }
}
