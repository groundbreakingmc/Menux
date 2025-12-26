package com.github.groundbreakingmc.menux.reqirements.condition.impl.basic;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirements.condition.MenuCondition;
import com.github.groundbreakingmc.menux.reqirements.value.ValueProvider;
import org.jetbrains.annotations.NotNull;

public final class BooleanCondition implements MenuCondition {

    private final ValueProvider provider;

    public BooleanCondition(@NotNull ValueProvider provider) {
        this.provider = provider;
    }

    @Override
    public boolean test(@NotNull MenuContext context) {
        final Object value = this.provider.value(context);

        if (value instanceof Boolean bool) {
            return bool;
        }

        throw new IllegalStateException(
                "Expected boolean value but got: " +
                        (value == null ? "null" : value.getClass().getSimpleName())
        );
    }
}
