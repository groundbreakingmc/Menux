package com.github.groundbreakingmc.menux.reqirement.function;

import com.github.groundbreakingmc.menux.reqirement.value.ValueProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface FunctionDefinition {
    @NotNull ValueProvider create(@NotNull List<ValueProvider> arguments);
}
