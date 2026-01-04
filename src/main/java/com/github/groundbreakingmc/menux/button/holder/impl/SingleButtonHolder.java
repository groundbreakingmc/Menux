package com.github.groundbreakingmc.menux.button.holder.impl;

import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SingleButtonHolder implements ButtonHolder {

    private final ButtonTemplate template;

    public SingleButtonHolder(@NotNull ButtonTemplate template) {
        this.template = template;
    }

    @Override
    public @NotNull ButtonTemplate button(@Nullable MenuContext context) {
        return this.template;
    }

    @Override
    public @NotNull List<ButtonTemplate> all() {
        return List.of(this.template);
    }
}
