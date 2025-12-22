package com.github.groundbreakingmc.menux.platform.player.impl;

import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.player.User;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.chat.SignedMessage;
import net.kyori.adventure.dialog.DialogLike;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class BukkitMenuPlayer implements MenuPlayer {

    private final Player nativePlayer;
    private final User user;
    private final Audience audience;

    public BukkitMenuPlayer(@NotNull Player nativePlayer, @NotNull Audience audience) {
        this.nativePlayer = nativePlayer;
        this.user = PacketEvents.getAPI().getPlayerManager().getUser(nativePlayer);
        this.audience = audience;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.nativePlayer.hasPermission(permission);
    }

    @Override
    public UUID uuid() {
        return this.user.getUUID();
    }

    @Override
    public ItemStack itemAt(int slot) {
        return SpigotConversionUtil.fromBukkitItemStack(this.nativePlayer.getInventory().getItem(slot));
    }

    @Override
    public boolean performCommand(@NotNull String command) {
        return this.nativePlayer.performCommand(command);
    }

    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.audience.sendMessage(source, message, type);
    }

    public void deleteMessage(@NotNull SignedMessage.Signature signature) {
        this.audience.deleteMessage(signature);
    }

    public void sendActionBar(@NotNull Component message) {
        this.audience.sendActionBar(message);
    }

    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        this.audience.sendPlayerListHeaderAndFooter(header, footer);
    }

    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        this.audience.sendTitlePart(part, value);
    }

    public void clearTitle() {
        this.audience.clearTitle();
    }

    public void resetTitle() {
        this.audience.resetTitle();
    }

    public void showBossBar(@NotNull BossBar bar) {
        this.audience.showBossBar(bar);
    }

    public void hideBossBar(@NotNull BossBar bar) {
        this.audience.hideBossBar(bar);
    }

    public void playSound(@NotNull Sound sound) {
        this.audience.playSound(sound);
    }

    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        this.audience.playSound(sound, x, y, z);
    }

    public void playSound(@NotNull Sound sound, @NotNull Sound.Emitter emitter) {
        this.audience.playSound(sound, emitter);
    }

    public void stopSound(@NotNull SoundStop stop) {
        this.audience.stopSound(stop);
    }

    public void openBook(@NotNull Book book) {
        this.audience.openBook(book);
    }

    public void sendResourcePacks(@NotNull ResourcePackRequest request) {
        this.audience.sendResourcePacks(request);
    }

    public void removeResourcePacks(@NotNull UUID id, @NotNull UUID @NotNull ... others) {
        this.audience.removeResourcePacks(id, others);
    }

    public void clearResourcePacks() {
        this.audience.clearResourcePacks();
    }

    public void showDialog(@NotNull DialogLike dialog) {
        this.audience.showDialog(dialog);
    }

    public void closeDialog() {
        this.audience.closeDialog();
    }

    @Override
    public @NotNull Object nativePlayer() {
        return this.nativePlayer;
    }

    @Override
    public @NotNull User user() {
        return this.user;
    }

    @NotNull
    public User packetEventsUser() {
        return this.user;
    }
}
