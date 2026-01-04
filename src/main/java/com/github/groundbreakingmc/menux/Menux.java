package com.github.groundbreakingmc.menux;

import com.github.groundbreakingmc.menux.action.registry.MenuActionRegistry;
import com.github.groundbreakingmc.menux.colorizer.impl.MiniMessageColorizer;
import com.github.groundbreakingmc.menux.menu.processor.MenuProcessor;
import com.github.groundbreakingmc.menux.menu.registry.impl.DefaultMenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.impl.PAPIParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.reqirements.parser.MenuRuleParserOptions;
import com.github.groundbreakingmc.menux.utils.ConfigurateMenuLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
        super.saveResource("menu.yml", false);

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
            final Player target = a.length < 1 ? (Player) s : Bukkit.getPlayer(a[0]);
            if (target == null) {
                s.sendMessage("§cPlayer not found!");
                return true;
            }
            final MenuPlayer player = MenuxAPI.playerFactory().create(target);
            final MenuProcessor processor = new MenuProcessor(new DefaultMenuRegistry(), player, menu);
            processor.open();

//            final Location location = target.getLocation().subtract(0, 3, 0);
//            final Vector3i position = new Vector3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
//
//            final var setSign = new WrapperPlayServerBlockChange(position, WrappedBlockState.getDefaultState(StateTypes.PALE_OAK_SIGN));
//
//            final WrappedBlockState wrappedBlockState = SpigotConversionUtil.fromBukkitBlockData(location.getBlock().getBlockData());
//            final var removeSign = new WrapperPlayServerBlockChange(position, wrappedBlockState);
//
//            final NBTCompound nbt = new NBTCompound();
//            nbt.setTag("is_waxed", new NBTByte((byte) 0));
//
//            final NBTCompound front = new NBTCompound();
//            front.setTag("color", new NBTString("red"));
//            front.setTag("has_glowing_text", new NBTByte((byte) 1));
//
//            final NBTList<NBTString> frontMessages = new NBTList<>(NBTType.STRING);
//            frontMessages.addTag(new NBTString(""));
//            frontMessages.addTag(new NBTString(""));
//            frontMessages.addTag(new NBTString("ВВЕДИТЕ"));
//            frontMessages.addTag(new NBTString("КОЛИЧЕВТСВО"));
//
//            front.setTag("messages", frontMessages);
//            nbt.setTag("front_text", front);
//
//            final var signData = new WrapperPlayServerBlockEntityData(position, BlockEntityTypes.SIGN, nbt);
//
//            final var openSign = new WrapperPlayServerOpenSignEditor(position, true);
//
//            MenuxAPI.playerFactory().create(target).user().sendPacket(setSign);
//            MenuxAPI.playerFactory().create(target).user().sendPacket(signData);
//            MenuxAPI.playerFactory().create(target).user().sendPacket(openSign);
//
//            Bukkit.getScheduler().runTaskLaterAsynchronously(this, () -> {
//                MenuxAPI.playerFactory().create(target).user().sendPacket(removeSign);
//            }, 3 * 20);
            return true;
        });
    }

    @Override
    public void onDisable() {
        MenuxAPI.terminate();
    }

    static {
        final Class<Menux> menux = Menux.class;
        final ClassLoader classLoader = menux.getClassLoader();

        final String jarPath = URLDecoder.decode(
                menux.getProtectionDomain().getCodeSource().getLocation().getPath(),
                StandardCharsets.UTF_8
        );

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
