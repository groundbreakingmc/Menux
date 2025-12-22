package com.github.groundbreakingmc.menux.reqirement.value;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ValueProvider {

    @Nullable Object value(@NotNull MenuContext context);
}
