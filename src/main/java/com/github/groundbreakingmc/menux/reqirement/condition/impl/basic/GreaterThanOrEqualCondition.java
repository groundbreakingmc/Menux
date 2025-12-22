package com.github.groundbreakingmc.menux.reqirement.condition.impl.basic;


import com.github.groundbreakingmc.menux.reqirement.value.ValueProvider;
import org.jetbrains.annotations.NotNull;

public final class GreaterThanOrEqualCondition extends AbstractNumberCondition {

    public GreaterThanOrEqualCondition(@NotNull ValueProvider left,
                                       @NotNull ValueProvider right) {
        super(left, right);
    }

    @Override
    protected boolean compare(Number left, Number right) {
        return left.doubleValue() >= right.doubleValue();
    }
}
