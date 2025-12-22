package com.github.groundbreakingmc.menux.menu.instance;

import com.github.groundbreakingmc.menux.buttons.ButtonTemplate;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MenuInstance {

    boolean open();

    void handleClick(@NotNull WrapperPlayClientClickWindow packet);

    void handleClose();

    void updateTitle(@NotNull String title);

    void updateTitle(@NotNull Component title);

    void setButton(int slot, ButtonTemplate button);

    int containerId();

    @NotNull Colorizer colorizer();

    @NotNull PlaceholderParser placeholderParser();
}
