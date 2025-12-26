package com.github.groundbreakingmc.menux.menu.instance.impl;

import com.github.groundbreakingmc.menux.MenuxAPI;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.button.ButtonProcessor;
import com.github.groundbreakingmc.menux.button.ButtonTemplate;
import com.github.groundbreakingmc.menux.click.ClickType;
import com.github.groundbreakingmc.menux.colorizer.Colorizer;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.registry.MenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.PlaceholderParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.reqirements.rule.MenuRule;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClickWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetCursorItem;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

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
        final int clickedSlot = packet.getSlot();
        if (clickedSlot < 0) return; // player clicked outside inventory
        final int topSize = this.menuTemplate.size();
        if (clickedSlot > topSize + 36) return; // possible crash

        final ClickType clickType = ClickType.detect(packet);
        final boolean clickedInMenu = clickedSlot < topSize;
        final ItemStack clicked;

        if (clickedInMenu) {
            final ButtonProcessor processor = this.buttons[clickedSlot];
            if (processor != null) {
                processor.processClick(this.clickContext(processor.template(), clickedSlot, clickType), clickType);
                clicked = this.itemCache[clickedSlot];
            } else {
                clicked = ItemStack.EMPTY;
            }
            this.sendSlot(this.containerId, clickedSlot, clicked);
        } else {
            clicked = null;
        }

        switch (clickType) {
            case LEFT_CLICK, RIGHT_CLICK, DOUBLE_CLICK -> {
                if (clickedInMenu) {
                    if (clicked != null) {
                        this.sendCursor(ItemStack.EMPTY);
                    }
                } else {
                    final int playerSlot = clickedSlot - topSize;
                    final ItemStack itemInClickedSlot = this.player.itemAt(playerSlot < 27 ? playerSlot + 9 : playerSlot - 27);
                    this.sendCursor(ItemStack.EMPTY);
                    this.sendSlot(0, playerSlot + 9, itemInClickedSlot);
                }
            }
            case ACTIONBAR_1, ACTIONBAR_2, ACTIONBAR_3, ACTIONBAR_4, ACTIONBAR_5, ACTIONBAR_6, ACTIONBAR_7, ACTIONBAR_8, ACTIONBAR_9 -> {
                final int actionBarSlot = topSize + 27 + packet.getButton();
                if (actionBarSlot != clickedSlot) { // if it is true, then nothing changed
                    final ItemStack item = this.player.itemAt(packet.getButton());
                    this.sendSlot(0, actionBarSlot, item);
                    if (clickedSlot >= topSize) { // clicked in own inventory
                        final int playerSlot = clickedSlot - topSize;
                        final ItemStack itemInClickedSlot = this.player.itemAt(playerSlot < 27 ? playerSlot + 9 : playerSlot - 27);
                        this.sendSlot(0, playerSlot + 9, itemInClickedSlot);
                    }
                }
            }
            case OFFHAND -> {
                this.resendOffhand();
                if (clickedSlot >= topSize) {
                    this.resendEffectedSlot(clickedSlot - topSize);
                }
            }
            case DROP, CONTROL_DROP -> {
                if (clickedSlot >= topSize) {
                    this.resendEffectedSlot(clickedSlot - topSize);
                }
            }
            case SHIFT_LEFT, SHIFT_RIGHT, QUICK_CRAFT -> {
                final Set<Integer> effectedSlots = packet.getSlots().isPresent()
                        ? packet.getSlots().get().keySet()
                        : packet.getHashedSlots() != null ? packet.getHashedSlots().keySet() : null;
                if (effectedSlots != null) {
                    if (effectedSlots.isEmpty()) return;
                    for (final int effectedSlot : packet.getHashedSlots().keySet()) {
                        if (effectedSlot < topSize) {
                            this.sendSlot(this.containerId, effectedSlot, this.itemCache[effectedSlot]);
                        } else {
                            this.resendEffectedSlot(effectedSlot - topSize);
                        }
                    }
                } else {
                    this.resendHashed();
                    this.resendInventory();
                }
            }
            default -> {
                this.resendHashed();
                this.resendInventory();
            }
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

    @Override
    public void setButton(int slot, @NotNull ButtonTemplate button) {
        this.sendButton(this.simpleContext, button, slot);
    }

    @Override
    public int containerId() {
        return this.containerId;
    }

    @Override
    public @NotNull MenuTemplate template() {
        return this.menuTemplate;
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

    private void sendSlot(int container, int slot, ItemStack item) {
        this.player.user().sendPacket(new WrapperPlayServerSetSlot(
                container,
                this.incrementStateId(),
                slot,
                item != null ? item : ItemStack.EMPTY
        ));
    }

    private void sendCursor(ItemStack item) {
        this.player.user().sendPacket(new WrapperPlayServerSetCursorItem(item));
    }

    private void resendEffectedSlot(int playerSlot) {
        final ItemStack itemInEffectedSlot = this.player.itemAt(playerSlot < 27 ? playerSlot + 9 : playerSlot - 27);
        this.sendSlot(0, playerSlot + 9, itemInEffectedSlot);
    }

    private void resendHashed() {
        final User user = this.player.user();
        for (int i = 0; i < this.itemCache.length; i++) {
            final ItemStack item = this.itemCache[i];
            if (item != null) {
                user.sendPacketSilently(new WrapperPlayServerSetSlot(
                        this.containerId, this.incrementStateId(), i, item
                ));
            }
        }
    }

    private void resendInventory() {
        final User user = this.player.user();
        for (int i = 0; i <= 35; i++) {
            final ItemStack item = this.player.itemAt(i);
            if (item != null) {
                user.sendPacketSilently(new WrapperPlayServerSetSlot(
                        0, this.incrementStateId(), i + 9, item
                ));
            }
        }
        this.resendOffhand();
    }

    private void resendOffhand() {
        final ItemStack item = this.player.itemAt(45);
        this.sendSlot(0, 45, item);
    }

    private int incrementStateId() {
        this.stateId = this.stateId + 1 & 32767;
        return this.stateId;
    }
}
