package com.github.groundbreakingmc.menux.action;

import com.github.groundbreakingmc.menux.action.registry.MenuActionRegistry;
import com.github.groundbreakingmc.menux.reqirement.parser.MenuRuleParserOptions;
import org.jetbrains.annotations.NotNull;

/**
 * Context containing all dependencies required for action creation.
 * This allows factories to access shared resources without polluting method signatures.
 */
public record ActionCreationContext(
        @NotNull MenuActionRegistry actionRegistry,
        @NotNull MenuRuleParserOptions ruleParserOptions
) {
}
