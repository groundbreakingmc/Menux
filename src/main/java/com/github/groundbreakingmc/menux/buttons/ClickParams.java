package com.github.groundbreakingmc.menux.buttons;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ClickParams(
        @NotNull List<MenuRule> conditions,
        @NotNull List<MenuAction> actions
) {
}
