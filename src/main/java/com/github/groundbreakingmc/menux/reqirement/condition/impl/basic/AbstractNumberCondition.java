package com.github.groundbreakingmc.menux.reqirement.condition.impl.basic;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.reqirement.condition.MenuCondition;
import com.github.groundbreakingmc.menux.reqirement.value.ValueProvider;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractNumberCondition implements MenuCondition {

    protected final ValueProvider left;
    protected final ValueProvider right;

    protected AbstractNumberCondition(@NotNull ValueProvider left, @NotNull ValueProvider right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean test(@NotNull MenuContext context) {
        final Number leftVal = toNumber(left.value(context), "left");
        final Number rightVal = toNumber(right.value(context), "right");
        return compare(leftVal, rightVal);
    }

    protected abstract boolean compare(Number left, Number right);

    protected Number toNumber(Object obj, String side) {
        if (obj instanceof Number number) return number;
        try {
            return Double.parseDouble(obj.toString());
        } catch (NumberFormatException | NullPointerException ex) {
            throw new IllegalStateException(
                    "Cannot compare non-numeric value (" + side + "): " + obj
            );
        }
    }
}
