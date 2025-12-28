package com.github.groundbreakingmc.menux;

import com.github.groundbreakingmc.menux.action.registry.MenuActionRegistry;
import com.github.groundbreakingmc.menux.colorizer.impl.MiniMessageColorizer;
import com.github.groundbreakingmc.menux.menu.registry.impl.DefaultMenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.impl.PAPIParser;
import com.github.groundbreakingmc.menux.reqirements.parser.MenuRuleParserOptions;
import com.github.groundbreakingmc.menux.utils.ConfigurateMenuLoader;
import com.github.retrooper.packetevents.protocol.nbt.*;
import com.github.retrooper.packetevents.protocol.world.blockentity.BlockEntityTypes;
import com.github.retrooper.packetevents.protocol.world.states.WrappedBlockState;
import com.github.retrooper.packetevents.protocol.world.states.type.StateTypes;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockChange;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerBlockEntityData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenSignEditor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings("unused")
public final class Menux extends JavaPlugin {

    @Override
    public void onEnable() {
        MenuxAPI.init();
        // TODO: remove command after testing

        final MenuTemplate menu;
        try {
            menu = ConfigurateMenuLoader.load(
                    new DefaultMenuRegistry(),
                    new MenuActionRegistry(),
                    MenuRuleParserOptions.DEFAULT,
                    YamlConfigurationLoader.builder()
                            .file(super.getDataFolder().toPath().resolve("menu.yml").toFile())
                            .build()
                            .load(),
                    new MiniMessageColorizer(),
                    new PAPIParser()
            );
        } catch (ConfigurateException ex) {
            throw new RuntimeException(ex);
        }

        Objects.requireNonNull(super.getCommand("menux")).setExecutor((s, c, l, a) -> {
            final Object target = a.length < 1 ? s : Bukkit.getPlayer(a[0]);
//            final MenuPlayer player = MenuxAPI.playerFactory().create(target);
//            final MenuInstance menuInst = menu.createMenu(player);
//            menuInst.open();
            if (target == null) {
                s.sendMessage("§cPlayer not found!");
                return true;
            }
            final var setSign = new WrapperPlayServerBlockChange(Vector3i.zero(), WrappedBlockState.getDefaultState(StateTypes.OAK_SIGN));
            final var removeSign = new WrapperPlayServerBlockChange(Vector3i.zero(), WrappedBlockState.getDefaultState(StateTypes.AIR));

            final NBTCompound nbt = new NBTCompound();
            nbt.setTag("is_waxed", new NBTByte((byte) 0));

            final NBTCompound front = new NBTCompound();
            front.setTag("color", new NBTString("black"));
            front.setTag("has_glowing_text", new NBTByte((byte) 0));

            final NBTList<NBTString> frontMessages = new NBTList<>(NBTType.STRING);
            frontMessages.addTag(new NBTString("{\"Line1\"}"));
            frontMessages.addTag(new NBTString("{\"Line2\"}"));
            frontMessages.addTag(new NBTString("{\"Line3\"}"));
            frontMessages.addTag(new NBTString("{\"Line4\"}"));

            front.setTag("messages", frontMessages);
            nbt.setTag("front_text", front);

            final var signData = new WrapperPlayServerBlockEntityData(Vector3i.zero(), BlockEntityTypes.SIGN, nbt);

            final var openSign = new WrapperPlayServerOpenSignEditor(Vector3i.zero(), true);

            MenuxAPI.playerFactory().create(target).user().sendPacket(setSign);
            MenuxAPI.playerFactory().create(target).user().sendPacket(signData);
            MenuxAPI.playerFactory().create(target).user().sendPacket(openSign);
            MenuxAPI.playerFactory().create(target).user().sendPacket(removeSign);
            return true;
        });
    }

    @Override
    public void onDisable() {
        MenuxAPI.terminate();
    }

    static {
        final Class<Menux> menux = Menux.class;
        final String jarPath = menux.getProtectionDomain().getCodeSource().getLocation().getPath();
        final ClassLoader classLoader = menux.getClassLoader();
        try (final JarFile jarFile = new JarFile(jarPath)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String entryName = entry.getName();

                if (entryName.endsWith(".class")
                        && entryName.startsWith("com/github/groundbreakingmc/menux")) {
                    final String className = entryName
                            .replace("/", ".")
                            .replace(".class", "");
                    classLoader.loadClass(className);
                }
            }
        } catch (Throwable th) {
            throw new RuntimeException(th);
        }
    }
}
