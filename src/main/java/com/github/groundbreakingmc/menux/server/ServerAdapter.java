package com.github.groundbreakingmc.menux.server;

import org.jetbrains.annotations.NotNull;

public interface ServerAdapter {

    boolean performCommand(@NotNull String command);

}
