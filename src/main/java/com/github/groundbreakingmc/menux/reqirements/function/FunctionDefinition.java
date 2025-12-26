package com.github.groundbreakingmc.menux.reqirements.function;

import com.github.groundbreakingmc.menux.reqirements.value.ValueProvider;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@FunctionalInterface
public interface FunctionDefinition {
    @NotNull ValueProvider create(@NotNull List<ValueProvider> arguments);
}
