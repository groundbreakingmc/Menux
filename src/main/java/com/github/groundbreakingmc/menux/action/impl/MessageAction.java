package com.github.groundbreakingmc.menux.action.impl;

import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.menu.processor.MenuProcessor;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Action that sends a formatted message to the player.
 * The message supports placeholders and color codes.
 */
public final class MessageAction implements MenuAction {

    private final String message;

    public MessageAction(String message) {
        this.message = message;
    }

    @Override
    public void run(@NotNull MenuContext context) {
        final MenuProcessor menu = context.menuInst();
        final String message = this.message.replace("{player}", context.player().user().getName());
        final Component parsed = menu.colorizer().colorizer(
                menu.placeholderParser().parse(context.player(), message)
        );
        context.player().sendMessage(parsed);
    }

    public static class Factory implements MenuAction.Factory {
        /**
         * Creates a new MessageAction instance from configuration data.
         *
         * <p>Required parameters:
         * <ul>
         *   <li>{@code message} (String) - the message to send to the player</li>
         * </ul>
         *
         * <p>The message will be processed for placeholders and color codes before being sent.
         *
         * @param context the creation context (unused for this action)
         * @param rawData the raw configuration data containing the message
         * @return a new MessageAction instance
         * @throws ActionCreateException if the message parameter is missing or invalid
         */
        @Override
        public @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData) throws ActionCreateException {
            final Object raw = rawData.get("message");
            if (raw == null) {
                throw new ActionCreateException("Missing required parameter 'message' in data: " + rawData);
            }

            return new MessageAction(raw.toString());
        }
    }
}
