package com.github.groundbreakingmc.menux.menu.registry.impl;

import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class DefaultMenuRegistry implements MenuRegistry {

    private final Object2ObjectMap<String, MenuTemplate> menus;

    public DefaultMenuRegistry() {
        this.menus = new Object2ObjectOpenHashMap<>();
    }

    @Override
    public boolean registerMenu(@NotNull String name, @NotNull MenuTemplate menuTemplate, boolean override) {
        if (!override && this.menus.containsKey(name)) {
            throw new RuntimeException(String.format(
                    "Menu with the name '%s' already exists",
                    name
            ));
        }

        return this.menus.put(name, menuTemplate) == null;
    }

    @Override
    public boolean unregisterMenu(@NotNull String name) {
        return this.menus.remove(name) != null;
    }

    @Override
    public MenuTemplate menu(@NotNull String name) {
        return this.menus.get(name);
    }

    @Override
    public @NotNull Map<String, MenuTemplate> registered() {
        return Object2ObjectMaps.unmodifiable(this.menus);
    }
}
