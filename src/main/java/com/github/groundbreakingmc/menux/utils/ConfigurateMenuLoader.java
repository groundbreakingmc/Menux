package com.github.groundbreakingmc.menux.utils;

import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.action.registry.MenuActionRegistry;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.button.ClickParams;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.menu.MenuType;
import com.github.groundbreakingmc.menux.menu.builder.DefaultMenuBuilder;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.reqirements.parser.MenuRuleParser;
import com.github.groundbreakingmc.menux.reqirements.parser.MenuRuleParserOptions;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.retrooper.packetevents.protocol.item.type.ItemType;
import com.github.retrooper.packetevents.protocol.item.type.ItemTypes;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.stream.Collectors;

public final class ConfigurateMenuLoader {

    private ConfigurateMenuLoader() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static MenuTemplate load(
            @NotNull MenuRegistry menuRegistry,
            @NotNull MenuActionRegistry actionRegistry,
            @NotNull MenuRuleParserOptions ruleParserOptions,
            @NotNull ConfigurationNode root,
            @NotNull Colorizer colorizer,
            @NotNull PlaceholderParser placeholderParser
    ) throws SerializationException {
        final ActionCreationContext creationContext = new ActionCreationContext(actionRegistry, ruleParserOptions);

        final DefaultMenuBuilder builder = MenuTemplate.builder(menuRegistry);
        builder.colorizer(colorizer);
        builder.placeholderParser(placeholderParser);

        builder.title(requireString(root.node("title"), "Menu title is required"));
        builder.type(parseMenuType(root.node("type")));

        builder.openRequirements(parseConditions(root.node("open-requirements"), creationContext));
        builder.openActions(parseActions(root.node("open-actions"), creationContext));
        builder.closeActions(parseActions(root.node("close-actions"), creationContext));

        final ConfigurationNode itemsNode = root.node("items");
        if (itemsNode.virtual()) {
            throw configError(itemsNode, "Missing 'items' section");
        }

        for (final var entry : itemsNode.childrenMap().entrySet()) {
            final String itemId = entry.getKey().toString();
            final ConfigurationNode itemNode = entry.getValue();

            final ButtonTemplate button = loadButton(itemNode, creationContext);

            final ConfigurationNode slotNode = itemNode.node("slot");
            if (!slotNode.virtual()) {
                builder.button(slotNode.getInt(), button);
                continue;
            }

            final ConfigurationNode slotsNode = itemNode.node("slots");
            if (slotsNode.virtual()) {
                throw configError(itemNode, "Either 'slot' or 'slots' must be specified for item '" + itemId + "'");
            }

            final List<String> slots = slotsNode.getList(String.class);
            if (slots == null || slots.isEmpty()) {
                throw configError(slotsNode, "'slots' must be a non-empty list. Example: [ \"0-8\" ]");
            }

            for (int i = 0; i < slots.size(); i++) {
                applySlot(slotsNode, slots.get(i), button, builder);
            }
        }

        return builder.build();
    }

    /* -------------------- Button -------------------- */

    private static ButtonTemplate loadButton(
            ConfigurationNode node,
            ActionCreationContext creationContext
    ) throws SerializationException {

        return ButtonTemplate.builder()
                .material(parseMaterial(node.node("material")))
                .amount(node.node("amount").getInt(1))
                .damage(node.node("damage").getInt(0))
                .renderPriority(node.node("render-priority").getInt(0))
                .displayName(node.node("display-name").getString())
                .lore(node.node("lore").getList(String.class))
                .viewRequirements(parseConditions(node.node("view-requirements"), creationContext))
                .clickActions(parseClickActions(node.node("actions"), creationContext))
                .build();
    }

    /* -------------------- Parsers -------------------- */

    private static ItemType parseMaterial(ConfigurationNode node) {
        final String raw = requireString(node, "Missing 'material'. Example: material: DIAMOND");

        final ItemType type = ItemTypes.getRegistry().getByName(raw.trim().toLowerCase());
        if (type == null) {
            throw configError(node, "Unknown material '" + raw + "'. Check spelling or version");
        }
        return type;
    }

    private static MenuType parseMenuType(ConfigurationNode node) {
        final String raw = requireString(node, "Missing 'type'. Example: GENERIC_9x3");
        try {
            return MenuType.fromString(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw configError(
                    node,
                    "Unknown menu type '" + raw + "'. Available: " +
                            Arrays.stream(MenuType.values())
                                    .map(Enum::name)
                                    .collect(Collectors.joining(", "))
            );
        }
    }

    private static List<MenuRule> parseConditions(ConfigurationNode node, ActionCreationContext creationContext) {
        final List<MenuRule> rules = new ArrayList<>();
        for (final ConfigurationNode condition : node.childrenList()) {
            final String expr = requireString(condition.node("expression"), "Condition requires 'expression'");
            final List<MenuAction> deny = parseActions(condition.node("deny-actions"), creationContext);
            try {
                rules.add(MenuRuleParser.parse(creationContext.ruleParserOptions(), expr, deny));
            } catch (IllegalArgumentException ex) {
                throw configError(condition.node("expression"), "Failed to parse expression: " + ex.getMessage());
            }
        }
        return rules;
    }

    private static Map<ClickType, ClickParams> parseClickActions(ConfigurationNode node, ActionCreationContext creationContext) {
        final Map<ClickType, ClickParams> map = new HashMap<>();
        for (final ConfigurationNode entry : node.childrenList()) {
            final String rawType = requireString(entry.node("type"), "Click action requires 'type'");
            final ClickType type;
            try {
                type = ClickType.valueOf(rawType.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw configError(entry.node("type"), "Unknown click type '" + rawType + "'");
            }

            map.put(type, new ClickParams(
                    parseConditions(entry.node("requirements"), creationContext),
                    parseActions(entry.node("execute"), creationContext)
            ));
        }
        return map;
    }

    private static List<MenuAction> parseActions(ConfigurationNode node, ActionCreationContext creationContext) {
        final List<MenuAction> actions = new ArrayList<>();
        for (final ConfigurationNode actionNode : node.childrenList()) {
            final String type = requireString(actionNode.node("type"), "Action requires 'type'");

            final Map<String, Object> data = new Object2ObjectOpenHashMap<>();
            for (final var entry : actionNode.childrenMap().entrySet()) {
                if ("type".equals(entry.getKey().toString())) continue;
                data.put(entry.getKey().toString(), entry.getValue().raw());
            }

            final MenuAction.Factory factory = creationContext.actionRegistry().get(type);
            if (factory == null) {
                throw configError(actionNode.node("type"), "Unknown action type '" + type + "'");
            }

            try {
                actions.add(factory.create(creationContext, data));
            } catch (Exception ex) {
                throw configError(actionNode, "Failed to create action '" + type + "': " + ex.getMessage());
            }
        }
        return actions;
    }

    /* -------------------- Slots -------------------- */

    private static void applySlot(ConfigurationNode context, String raw, ButtonTemplate button, DefaultMenuBuilder builder) {
        final String[] parts = raw.split("-");
        if (parts.length > 2) {
            throw configError(context, "Invalid slot format '" + raw + "'. Use '5' or '0-8'");
        }

        final int from = parseInt(context, parts[0]);
        final int to = parts.length == 2
                ? parseInt(context, parts[1])
                : from;

        if (from > to) {
            throw configError(context, "Invalid slot range '" + raw + "'. Start must be < end");
        }

        builder.fillRange(from, to, button);
    }

    private static String requireString(ConfigurationNode node, String message) {
        final String value = node.getString();
        if (value == null) throw configError(node, message);
        return value;
    }

    private static int parseInt(ConfigurationNode node, String raw) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw configError(node, "Expected number, got '" + raw + "'");
        }
    }

    /* -------------------- Errors -------------------- */

    private static IllegalStateException configError(ConfigurationNode node, String message) {
        return new IllegalStateException("Config error at '" + formatPath(node) + "': " + message);
    }

    private static String formatPath(ConfigurationNode node) {
        if (node == null) return "<unknown>";
        final Iterator<Object> iterator = node.path().iterator();
        if (!iterator.hasNext()) return "<root>";
        final StringBuilder path = new StringBuilder(node.path().size() * 8);
        while (iterator.hasNext()) {
            path.append(iterator.next()).append('.');
        }
        path.setLength(path.length() - 1); // remove last dot
        return path.toString();
    }
}
