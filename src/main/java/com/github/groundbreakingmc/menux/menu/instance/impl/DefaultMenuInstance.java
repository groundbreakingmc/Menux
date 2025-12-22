package com.github.groundbreakingmc.menux.menu.instance.impl;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.buttons.ButtonProcessor;
import com.github.groundbreakingmc.menux.buttons.ButtonTemplate;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.reqirement.rule.MenuRule;
import com.github.retrooper.packetevents.protocol.item.HashedStack;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public final class DefaultMenuInstance implements MenuInstance {

    private final MenuRegistry menuRegistry;
    private final MenuPlayer player;
    private final MenuTemplate menuTemplate;
    private final int containerId;
    private final ItemStack[] itemCache;
    private final ButtonProcessor[] buttons;
    private final MenuContext simpleContext;

    private boolean opened;
    private int stateId;

    public DefaultMenuInstance(MenuRegistry menuRegistry, MenuPlayer player, MenuTemplate menuTemplate, int containerId) {
        this.menuRegistry = menuRegistry;
        this.player = player;
        this.menuTemplate = menuTemplate;
        this.containerId = containerId;
        this.itemCache = new ItemStack[menuTemplate.size()];
        this.buttons = new ButtonProcessor[menuTemplate.size()];
        this.simpleContext = new MenuContext(player, menuRegistry, this);
    }

    public boolean open() {
        if (this.opened) throw new IllegalStateException("This menu is already opened!");

        List<MenuAction> actions = this.menuTemplate.openActions();
        boolean shouldOpen = true;

        final List<MenuRule> openRequirements = this.menuTemplate.openRequirements();
        for (final MenuRule rule : openRequirements) {
            if (!rule.test(this.simpleContext)) {
                actions = rule.denyActions();
                shouldOpen = false;
                break;
            }
        }

        MenuxAPI.menuManager().registerMenu(this.player, this);

        if (shouldOpen) {
            this.sendMenu();
        } else {
            MenuxAPI.menuManager().unregisterMenu(this.player.uuid());
        }

        if (!actions.isEmpty()) {
            for (final MenuAction action : actions) {
                action.run(this.simpleContext);
            }
        }

        return shouldOpen;
    }

    @Override
    public void handleClick(@NotNull WrapperPlayClientClickWindow packet) {
        final ButtonProcessor processor = packet.getSlot() < this.buttons.length ? this.buttons[packet.getSlot()] : null;
        if (processor != null) {
            final ClickType clickType = ClickType.detect(packet);
            final int slot = packet.getSlot();
            processor.processClick(this.clickContext(processor.template(), slot, clickType), clickType);

            final ItemStack clicked = this.itemCache[slot];
            switch (clickType) {
                case LEFT_CLICK, RIGHT_CLICK -> {
                    this.player.user().sendPacket(new WrapperPlayServerSetSlot(this.containerId, this.incrementStateId(), slot, clicked));
                    this.player.user().sendPacket(new WrapperPlayServerSetCursorItem(ItemStack.EMPTY));
                }
                case ACTIONBAR_1, ACTIONBAR_2, ACTIONBAR_3, ACTIONBAR_4, ACTIONBAR_5, ACTIONBAR_6, ACTIONBAR_7, ACTIONBAR_8, ACTIONBAR_9 -> {
                    final Optional<HashedStack> hashedStack = packet.getHashedSlots().get(packet.getSlot());
                    if (hashedStack != null) {
                        this.player.user().sendPacket(new WrapperPlayServerSetSlot(
                                0,
                                this.incrementStateId(),
                                this.menuTemplate.size() + 27 + packet.getButton(),
                                hashedStack.isPresent() ? hashedStack.get().asItemStack() : ItemStack.EMPTY
                        ));
                    }
                }
                case OFFHAND -> {
                    final Optional<HashedStack> hashedStack = packet.getHashedSlots().get(packet.getSlot());
                    if (hashedStack != null) {
                        this.player.user().sendPacket(new WrapperPlayServerSetSlot(
                                0,
                                this.incrementStateId(),
                                45,
                                hashedStack.isPresent() ? hashedStack.get().asItemStack() : ItemStack.EMPTY
                        ));
                    }
                }
                default -> {
                    for (final int effectedSlot : packet.getHashedSlots().keySet()) {
                        this.player.user().sendPacket(new WrapperPlayServerSetSlot(this.containerId, this.incrementStateId(), effectedSlot, this.player.itemAt(effectedSlot)));
                    }
                }
            }
            this.player.user().sendPacket(new WrapperPlayServerSetSlot(this.containerId, this.incrementStateId(), slot, clicked));
        }
    }

    @Override
    public void handleClose() {
        final List<MenuAction> menuActions = this.menuTemplate.closeActions();
        if (menuActions.isEmpty()) return;
        for (final MenuAction action : menuActions) {
            action.run(this.simpleContext);
        }
    }

    @Override
    public void updateTitle(@NotNull String title) {
        if (!this.opened) throw new IllegalStateException("Can't update title for not opened menu!");
        this.updateTitle(this.colorizer().colorizer(
                this.placeholderParser().parse(this.player, title)
        ));
    }

    @Override
    public void updateTitle(@NotNull Component title) {
        if (!this.opened) throw new IllegalStateException("Can't update title for not opened menu!");
        final User user = this.player.user();
        user.sendPacketSilently(new WrapperPlayServerOpenWindow(
                this.containerId, this.menuTemplate.type().ordinal(), title
        ));
        for (int i = 0; i < this.itemCache.length; i++) {
            final ItemStack item = this.itemCache[i];
            if (item != null) {
                user.sendPacketSilently(new WrapperPlayServerSetSlot(
                        this.containerId, this.incrementStateId(), i, item
                ));
            }
        }
    }

    public void setButton(int slot, ButtonTemplate button) {
        this.sendButton(this.simpleContext, button, slot);
    }

    @Override
    public int containerId() {
        return this.containerId;
    }

    @Override
    public @NotNull Colorizer colorizer() {
        return this.menuTemplate.colorizer();
    }

    @Override
    public @NotNull PlaceholderParser placeholderParser() {
        return this.menuTemplate.placeholderParser();
    }

    private void sendMenu() {
        final User user = this.player.user();
        user.sendPacketSilently(new WrapperPlayServerOpenWindow(this.containerId,
                this.menuTemplate.type().ordinal(),
                this.menuTemplate.title(this.player)
        ));

        for (int i = 0; i < this.buttons.length; i++) {
            final ButtonTemplate button = this.menuTemplate.buttonAt(i, this.simpleContext);
            if (button == null) continue;
            this.sendButton(this.simpleContext, button, i);
        }
        this.opened = true;
    }

    private void sendButton(MenuContext context, ButtonTemplate button, int slot) {
        final ItemStack item = button.createItem(context);
        this.itemCache[slot] = item;
        this.buttons[slot] = new ButtonProcessor(button);
        context.player().user().sendPacketSilently(new WrapperPlayServerSetSlot(
                this.containerId,
                this.incrementStateId(),
                slot,
                item
        ));
    }

    public MenuContext clickContext(ButtonTemplate clickedButton, int clickedSlot, ClickType clickType) {
        return new MenuContext(this.player, this.menuRegistry, this, clickedButton, clickedSlot, clickType);
    }

    private int incrementStateId() {
        this.stateId = this.stateId + 1 & 32767;
        return this.stateId;
    }
}
