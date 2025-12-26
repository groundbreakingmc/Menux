package com.github.groundbreakingmc.menux.action.impl;

import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.ClickParams;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.parser.MenuRuleParser;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Action that fills specified slots in the menu with a button template.
 */
public final class FillAction implements MenuAction {

    private final ButtonTemplate button;
    private final IntList slots;

    public FillAction(ButtonTemplate button, IntList slots) {
        this.button = button;
        this.slots = slots;
    }

    @Override
    public void run(@NotNull MenuContext context) {
        for (int i = 0; i < this.slots.size(); i++) {
            final int slot = this.slots.getInt(i);
            context.menuInst().setButton(slot, this.button);
        }
    }

    public static class Factory implements MenuAction.Factory {

        /**
         * Creates a new FillAction instance from configuration data.
         *
         * <p>Required parameters:
         * <ul>
         *   <li>{@code slots} (List&lt;String&gt;) - slot ranges to fill (e.g., ["0-8", "9"])</li>
         *   <li>{@code button} (Map) - button template configuration</li>
         * </ul>
         *
         * <p>Button configuration supports:
         * <ul>
         *   <li>{@code material} (String) - the item material</li>
         *   <li>{@code amount} (Integer) - stack size (default: 1)</li>
         *   <li>{@code display-name} (String) - the display name</li>
         *   <li>{@code lore} (List&lt;String&gt;) - item lore lines</li>
         *   <li>{@code view-requirements} (List) - visibility conditions</li>
         *   <li>{@code actions} (List) - click action configurations</li>
         * </ul>
         *
         * @param context the creation context containing action registry and rule parser
         * @param rawData the raw configuration data containing button and slot information
         * @return a new FillAction instance
         * @throws ActionCreateException if required parameters are missing or invalid
         */
        @Override
        public @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData) throws ActionCreateException {
            final IntList slots = parseSlots(rawData);
            final ButtonTemplate button = parseButton(context, rawData);
            return new FillAction(button, slots);
        }

        private static IntList parseSlots(Map<String, Object> rawData) throws ActionCreateException {
            final Object slotsRaw = rawData.get("slots");
            if (slotsRaw == null) {
                throw new ActionCreateException("Missing required parameter 'slots'");
            }

            if (!(slotsRaw instanceof List<?> slotsList)) {
                throw new ActionCreateException("Parameter 'slots' must be a List, got: " + slotsRaw.getClass().getSimpleName());
            }

            final IntList result = new IntArrayList();
            for (final Object slotRaw : slotsList) {
                final String slotStr = String.valueOf(slotRaw);
                parseSlotRange(slotStr, result);
            }

            if (result.isEmpty()) {
                throw new ActionCreateException("'slots' list cannot be empty");
            }

            return result;
        }

        private static void parseSlotRange(String raw, IntList target) throws ActionCreateException {
            final String[] parts = raw.split("-");
            if (parts.length > 2) {
                throw new ActionCreateException("Invalid slot format '" + raw + "'. Use '5' or '0-8'");
            }

            try {
                final int from = Integer.parseInt(parts[0].trim());
                final int to = parts.length == 2 ? Integer.parseInt(parts[1].trim()) : from;

                if (from > to) {
                    throw new ActionCreateException("Invalid slot range '" + raw + "'. Start must be <= end");
                }

                for (int i = from; i <= to; i++) {
                    target.add(i);
                }
            } catch (NumberFormatException e) {
                throw new ActionCreateException("Invalid slot number in '" + raw + "'");
            }
        }

        @SuppressWarnings("unchecked")
        private static ButtonTemplate parseButton(ActionCreationContext context, Map<String, Object> rawData) throws ActionCreateException {
            final Object buttonRaw = rawData.get("button");
            if (buttonRaw == null) {
                throw new ActionCreateException("Missing required parameter 'button'");
            }

            if (!(buttonRaw instanceof Map<?, ?> buttonMap)) {
                throw new ActionCreateException("Parameter 'button' must be a Map, got: " + buttonRaw.getClass().getSimpleName());
            }

            final Map<String, Object> buttonData = (Map<String, Object>) buttonMap;

            return ButtonTemplate.builder()
                    .material(parseMaterial(buttonData))
                    .amount(parseAmount(buttonData))
                    .renderPriority(parseRenderPriority(buttonData))
                    .displayName(parseDisplayName(buttonData))
                    .lore(parseLore(buttonData))
                    .viewRequirements(parseViewRequirements(context, buttonData))
                    .clickActions(parseClickActions(context, buttonData))
                    .build();
        }

        private static ItemType parseMaterial(Map<String, Object> buttonData) throws ActionCreateException {
            final Object materialRaw = buttonData.get("material");
            if (materialRaw == null) {
                throw new ActionCreateException("Missing required 'material' in button configuration");
            }

            final String materialStr = String.valueOf(materialRaw).trim().toLowerCase();
            final ItemType type = ItemTypes.getRegistry().getByName(materialStr);
            if (type == null) {
                throw new ActionCreateException("Unknown material '" + materialStr + "'");
            }

            return type;
        }

        private static int parseAmount(Map<String, Object> buttonData) {
            final Object amountRaw = buttonData.get("amount");
            if (amountRaw == null) return 1;
            if (!(amountRaw instanceof Number num)) {
                throw new ActionCreateException("Parameter 'amount' must be a Integer, got: " + amountRaw.getClass().getSimpleName());
            }
            return num.intValue();
        }

        private static int parseRenderPriority(Map<String, Object> buttonData) {
            final Object amountRaw = buttonData.get("render-priority");
            if (amountRaw == null) return 0;
            if (!(amountRaw instanceof Number num)) {
                throw new ActionCreateException("Parameter 'render-priority' must be a Integer, got: " + amountRaw.getClass().getSimpleName());
            }
            return num.intValue();
        }

        private static String parseDisplayName(Map<String, Object> buttonData) {
            final Object nameRaw = buttonData.get("display-name");
            return nameRaw != null ? String.valueOf(nameRaw) : "";
        }

        private static List<String> parseLore(Map<String, Object> buttonData) {
            final Object loreRaw = buttonData.get("lore");
            if (loreRaw instanceof List<?> loreList) {
                final List<String> result = new ArrayList<>(loreList.size());
                for (final Object line : loreList) {
                    result.add(String.valueOf(line));
                }
                return result;
            }
            return List.of();
        }

        @SuppressWarnings("unchecked")
        private static List<MenuRule> parseViewRequirements(ActionCreationContext context, Map<String, Object> buttonData) throws ActionCreateException {
            final Object requirementsRaw = buttonData.get("view-requirements");
            if (!(requirementsRaw instanceof List<?> requirementsList)) {
                return List.of();
            }

            final List<MenuRule> rules = new ArrayList<>();
            for (final Object reqRaw : requirementsList) {
                if (!(reqRaw instanceof Map<?, ?> reqMap)) {
                    continue;
                }

                final Map<String, Object> reqData = (Map<String, Object>) reqMap;
                final Object exprRaw = reqData.get("expression");
                if (exprRaw == null) {
                    throw new ActionCreateException("View requirement missing 'expression'");
                }

                final String expression = String.valueOf(exprRaw);
                final List<MenuAction> denyActions = parseDenyActions(context, reqData);

                try {
                    rules.add(MenuRuleParser.parse(context.ruleParserOptions(), expression, denyActions));
                } catch (IllegalArgumentException e) {
                    throw new ActionCreateException("Failed to parse requirement expression: " + e.getMessage());
                }
            }

            return rules;
        }

        @SuppressWarnings("unchecked")
        private static List<MenuAction> parseDenyActions(ActionCreationContext context, Map<String, Object> reqData) throws ActionCreateException {
            final Object denyRaw = reqData.get("deny-actions");
            if (!(denyRaw instanceof List<?> denyList)) {
                return List.of();
            }

            final List<MenuAction> actions = new ArrayList<>();
            for (final Object actionRaw : denyList) {
                if (!(actionRaw instanceof Map<?, ?> actionMap)) {
                    continue;
                }

                final Map<String, Object> actionData = (Map<String, Object>) actionMap;
                final MenuAction action = createAction(context, actionData);
                actions.add(action);
            }

            return actions;
        }

        @SuppressWarnings("unchecked")
        private static Map<ClickType, ClickParams> parseClickActions(ActionCreationContext context, Map<String, Object> buttonData) throws ActionCreateException {
            final Object actionsRaw = buttonData.get("actions");
            if (!(actionsRaw instanceof List<?> actionsList)) {
                return Map.of();
            }

            final Map<ClickType, ClickParams> clickMap = new HashMap<>();

            for (final Object actionRaw : actionsList) {
                if (!(actionRaw instanceof Map<?, ?> actionMap)) {
                    continue;
                }

                final Map<String, Object> actionData = (Map<String, Object>) actionMap;
                final Object typeRaw = actionData.get("type");
                if (typeRaw == null) {
                    throw new ActionCreateException("Click action missing 'type'");
                }

                final ClickType clickType;
                try {
                    clickType = ClickType.valueOf(String.valueOf(typeRaw).toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new ActionCreateException("Unknown click type: " + typeRaw);
                }

                final List<MenuRule> requirements = parseClickRequirements(context, actionData);
                final List<MenuAction> executeActions = parseExecuteActions(context, actionData);

                clickMap.put(clickType, new ClickParams(requirements, executeActions));
            }

            return clickMap;
        }

        private static List<MenuRule> parseClickRequirements(ActionCreationContext context, Map<String, Object> actionData) throws ActionCreateException {
            final Object requirementsRaw = actionData.get("requirements");
            if (!(requirementsRaw instanceof List<?> reqList)) {
                return List.of();
            }

            return parseViewRequirements(context, Map.of("view-requirements", reqList));
        }

        @SuppressWarnings("unchecked")
        private static List<MenuAction> parseExecuteActions(ActionCreationContext context, Map<String, Object> actionData) throws ActionCreateException {
            final Object executeRaw = actionData.get("execute");
            if (!(executeRaw instanceof List<?> executeList)) {
                return List.of();
            }

            final List<MenuAction> actions = new ArrayList<>();
            for (final Object execRaw : executeList) {
                if (!(execRaw instanceof Map<?, ?> execMap)) {
                    continue;
                }

                final Map<String, Object> execData = (Map<String, Object>) execMap;
                final MenuAction action = createAction(context, execData);
                actions.add(action);
            }

            return actions;
        }

        private static MenuAction createAction(ActionCreationContext context, Map<String, Object> actionData) throws ActionCreateException {
            final Object typeRaw = actionData.get("type");
            if (typeRaw == null) {
                throw new ActionCreateException("Action missing 'type'");
            }

            final String type = String.valueOf(typeRaw);
            final MenuAction.Factory factory = context.actionRegistry().get(type);
            if (factory == null) {
                throw new ActionCreateException("Unknown action type: " + type);
            }

            return factory.create(context, actionData);
        }
    }
}
