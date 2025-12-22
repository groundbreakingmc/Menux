package com.github.groundbreakingmc.menux.reqirement.parser;

import com.github.groundbreakingmc.menux.reqirement.function.FunctionDefinition;
import com.github.groundbreakingmc.menux.reqirement.value.ValueProvider;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public record MenuRuleParserOptions(
        @NotNull String orIdentifier,
        @NotNull String andIdentifier,
        @NotNull String notIdentifier,
        @NotNull Map<String, FunctionDefinition> functions,
        @NotNull Map<String, ValueProvider> variables
) {

    public static final MenuRuleParserOptions DEFAULT = MenuRuleParserOptions.builder().build();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String orIdentifier = "||";
        private String andIdentifier = "&&";
        private String notIdentifier = "!";
        private final Map<String, FunctionDefinition> functions = new Object2ObjectOpenHashMap<>();
        private final Map<String, ValueProvider> variables = new Object2ObjectOpenHashMap<>();

        Builder() {

        }

        public Builder orIdentifier(@NotNull String orIdentifier) {
            this.orIdentifier = orIdentifier;
            return this;
        }

        public Builder andIdentifier(@NotNull String andIdentifier) {
            this.andIdentifier = andIdentifier;
            return this;
        }

        public Builder notIdentifier(@NotNull String notIdentifier) {
            this.notIdentifier = notIdentifier;
            return this;
        }

        public Map<String, FunctionDefinition> functions() {
            return Collections.unmodifiableMap(this.functions);
        }

        public Builder functions(@NotNull Map<String, FunctionDefinition> functions) {
            this.functions.clear();
            this.functions.putAll(functions);
            return this;
        }

        public Builder addFunction(@NotNull String name, @NotNull FunctionDefinition function) {
            this.functions.put(name, function);
            return this;
        }

        public Map<String, ValueProvider> variables() {
            return Collections.unmodifiableMap(this.variables);
        }

        public Builder variables(@NotNull Map<String, ValueProvider> variables) {
            this.variables.clear();
            this.variables.putAll(variables);
            return this;
        }

        public Builder addVariable(@NotNull String name, @NotNull ValueProvider provider) {
            this.variables.put(name, provider);
            return this;
        }

        public MenuRuleParserOptions build() {
            return new MenuRuleParserOptions(
                    this.orIdentifier,
                    this.andIdentifier,
                    this.notIdentifier,
                    ImmutableMap.copyOf(this.functions),
                    ImmutableMap.copyOf(this.variables)
            );
        }
    }
}
