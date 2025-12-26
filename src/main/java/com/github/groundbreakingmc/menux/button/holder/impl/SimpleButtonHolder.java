package com.github.groundbreakingmc.menux.button.holder.impl;

import com.github.groundbreakingmc.menux.button.ButtonHolder;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SimpleButtonHolder implements ButtonHolder {

    private final ButtonTemplate template;

    public SimpleButtonHolder(@NotNull ButtonTemplate template) {
        this.template = template;
    }

    @Override
    public @NotNull ButtonTemplate button(@Nullable MenuContext context) {
        return this.template;
    }
}
