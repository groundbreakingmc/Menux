package com.github.groundbreakingmc.menux.reqirement.condition;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface MenuCondition {

    boolean test(@NotNull MenuContext context);
}
