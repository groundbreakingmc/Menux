package com.github.groundbreakingmc.menux.action.impl;

import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Action that opens another menu for the player.
 * If the specified menu doesn't exist, the action does nothing.
 */
public final class OpenMenuAction implements MenuAction {

    private final String menuName;

    public OpenMenuAction(String menuName) {
        this.menuName = menuName;
    }

    @Override
    public void run(@NotNull MenuContext context) {
        final MenuTemplate menu = context.menuRegistry().menu(this.menuName);
        if (menu != null) {
            menu.createMenu(context.player()).open();
        }
    }

    public static class Factory implements MenuAction.Factory {
        /**
         * Creates a new OpenMenuAction instance from configuration data.
         *
         * <p>Required parameters:
         * <ul>
         *   <li>{@code menu-name} (String) - the name of the menu to open</li>
         * </ul>
         *
         * @param context the creation context (unused for this action)
         * @param rawData the raw configuration data containing the menu name
         * @return a new OpenMenuAction instance
         * @throws ActionCreateException if the menu-name parameter is missing or invalid
         */
        @Override
        public @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData) throws ActionCreateException {
            final Object raw = rawData.get("menu-name");
            if (raw == null) {
                throw new ActionCreateException("Missing required parameter 'menu-name' in data: " + rawData);
            }

            return new OpenMenuAction(raw.toString());
        }
    }
}
