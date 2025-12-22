package com.github.groundbreakingmc.menux.action.impl;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Action that closes the current menu by sending a close window packet to the player.
 */
public final class CloseMenuAction implements MenuAction {

    @Override
    public void run(@NotNull MenuContext context) {
        MenuxAPI.menuManager().handleClose(context.player(), context.menuInst().containerId());
    }

    public static class Factory implements MenuAction.Factory {
        /**
         * Creates a new CloseMenuAction instance.
         * This action requires no additional configuration.
         *
         * @param context the creation context (unused for this action)
         * @param rawData the raw configuration data (unused for this action)
         * @return a new CloseMenuAction instance
         * @throws ActionCreateException if action creation fails
         */
        @Override
        public @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData) throws ActionCreateException {
            return new CloseMenuAction();
        }
    }
}
