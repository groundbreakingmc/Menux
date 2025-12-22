package com.github.groundbreakingmc.menux.reqirement.condition.impl.logic;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirement.condition.MenuCondition;
import org.jetbrains.annotations.NotNull;

public final class NotCondition implements MenuCondition {

    private final MenuCondition condition;

    public NotCondition(MenuCondition condition) {
        this.condition = condition;
    }

    @Override
    public boolean test(@NotNull MenuContext context) {
        return !this.condition.test(context);
    }
}
