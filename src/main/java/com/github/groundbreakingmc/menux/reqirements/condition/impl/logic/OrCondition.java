package com.github.groundbreakingmc.menux.reqirements.condition.impl.logic;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.condition.MenuCondition;
import org.jetbrains.annotations.NotNull;

public final class OrCondition implements MenuCondition {

    private final MenuCondition left;
    private final MenuCondition right;

    public OrCondition(MenuCondition left, MenuCondition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean test(@NotNull MenuContext context) {
        return this.left.test(context) || this.right.test(context);
    }
}
