package com.github.groundbreakingmc.menux.reqirement.condition.impl.basic;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirement.condition.MenuCondition;
import com.github.groundbreakingmc.menux.reqirement.value.ValueProvider;
import org.jetbrains.annotations.NotNull;

public final class NotEqualsCondition implements MenuCondition {

    private final ValueProvider left;
    private final ValueProvider right;

    public NotEqualsCondition(ValueProvider left, ValueProvider right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean test(@NotNull MenuContext context) {
        final Object left = this.left.value(context);
        final Object right = this.right.value(context);
        return left == null || right == null ? left != right : !left.equals(right);
    }
}
