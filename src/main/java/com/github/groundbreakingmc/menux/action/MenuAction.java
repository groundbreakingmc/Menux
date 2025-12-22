package com.github.groundbreakingmc.menux.action;

import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface MenuAction {

    void run(@NotNull MenuContext context);

    @FunctionalInterface
    interface Factory {
        /**
         * Creates a new MenuAction instance from configuration data.
         *
         * @param context the creation context containing shared dependencies
         * @param rawData the raw configuration data specific to this action
         * @return a new MenuAction instance
         * @throws ActionCreateException if action creation fails
         */
        @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData) throws ActionCreateException;
    }
}
