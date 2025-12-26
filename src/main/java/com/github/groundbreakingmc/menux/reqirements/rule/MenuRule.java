package com.github.groundbreakingmc.menux.reqirements.rule;

import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.condition.MenuCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MenuRule {

    private final MenuCondition condition;
    private final List<MenuAction> denyActions;

    public MenuRule(MenuCondition condition, List<MenuAction> denyActions) {
        this.condition = condition;
        this.denyActions = denyActions;
    }

    public boolean test(@NotNull MenuContext context) {
        return this.condition.test(context);
    }

    public List<MenuAction> denyActions() {
        return this.denyActions;
    }
}
