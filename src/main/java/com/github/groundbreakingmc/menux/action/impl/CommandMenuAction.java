package com.github.groundbreakingmc.menux.action.impl;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Action that executes a command either as the player or as the console.
 */
public final class CommandMenuAction implements MenuAction {

    private final String command;
    private final boolean executeAsPlayer;

    public CommandMenuAction(String command, boolean executeAsPlayer) {
        this.command = command;
        this.executeAsPlayer = executeAsPlayer;
    }

    @Override
    public void run(@NotNull MenuContext context) {
        if (this.executeAsPlayer) {
            context.player().performCommand(this.command);
        } else {
            MenuxAPI.server().performCommand(this.command);
        }
    }

    public static class Factory implements MenuAction.Factory {
        /**
         * Creates a new CommandMenuAction instance from configuration data.
         *
         * <p>Required parameters:
         * <ul>
         *   <li>{@code command} (String) - the command to execute (without leading slash)</li>
         * </ul>
         *
         * <p>Optional parameters:
         * <ul>
         *   <li>{@code player} (Boolean) - whether to execute as player (default: false)</li>
         * </ul>
         *
         * @param context the creation context (unused for this action)
         * @param rawData the raw configuration data containing command and execution settings
         * @return a new CommandMenuAction instance
         * @throws ActionCreateException if required parameters are missing or invalid
         */
        @Override
        public @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData) throws ActionCreateException {
            final Object raw = rawData.get("command");
            if (raw == null) {
                throw new ActionCreateException("Missing required parameter 'command' in data: " + rawData);
            }
            if (!(raw instanceof String command)) {
                throw new ActionCreateException("Parameter 'command' must be a String, got: " + raw.getClass().getSimpleName());
            }

            final Object rawFromPlayer = rawData.getOrDefault("player", false);
            if (!(rawFromPlayer instanceof Boolean fromPlayer)) {
                throw new ActionCreateException("Parameter 'player' must be a Boolean, got: " + rawFromPlayer.getClass().getSimpleName());
            }

            return new CommandMenuAction(command, fromPlayer);
        }
    }
}
