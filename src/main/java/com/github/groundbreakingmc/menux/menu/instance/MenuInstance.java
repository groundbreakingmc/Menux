package com.github.groundbreakingmc.menux.menu.instance;

import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.managers.PlayerMenuManager;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MenuInstance {

    /**
     * Better use {@link PlayerMenuManager#open(MenuPlayer, MenuTemplate)} instead
     */
    boolean open();

    void handleClick(@NotNull WrapperPlayClientClickWindow packet);

    void handleClose();

    void updateTitle(@NotNull String title);

    void updateTitle(@NotNull Component title);

    void setButton(int slot, @NotNull ButtonTemplate button);

    int containerId();

    @NotNull MenuTemplate template();

    @NotNull Colorizer colorizer();

    @NotNull PlaceholderParser placeholderParser();
}
