package com.github.groundbreakingmc.menux.menu.registry;

import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface MenuRegistry {

    boolean registerMenu(@NotNull String name, @NotNull MenuTemplate menuTemplate, boolean override);

    boolean unregisterMenu(@NotNull String name);

    @Nullable MenuTemplate menu(@NotNull String name);

    @NotNull Map<String, MenuTemplate> registered();
}
