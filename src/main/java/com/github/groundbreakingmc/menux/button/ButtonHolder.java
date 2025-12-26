package com.github.groundbreakingmc.menux.button;

import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ButtonHolder {

    @Nullable ButtonTemplate button(@Nullable MenuContext context);

    default @NotNull List<ButtonTemplate> all() {
        final ButtonTemplate button = this.button(null);
        return button != null ? List.of(button) : List.of();
    }
}
