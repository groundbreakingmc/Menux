package com.github.groundbreakingmc.menux;

import com.github.groundbreakingmc.menux.action.registry.MenuActionRegistry;
import com.github.groundbreakingmc.menux.colorizer.impl.MiniMessageColorizer;
import com.github.groundbreakingmc.menux.menu.instance.MenuInstance;
import com.github.groundbreakingmc.menux.menu.registry.impl.DefaultMenuRegistry;
import com.github.groundbreakingmc.menux.menu.template.MenuTemplate;
import com.github.groundbreakingmc.menux.placeholder.impl.PAPIParser;
import com.github.groundbreakingmc.menux.platform.player.MenuPlayer;
import com.github.groundbreakingmc.menux.reqirements.parser.MenuRuleParserOptions;
import com.github.groundbreakingmc.menux.utils.ConfigurateMenuLoader;
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
            final MenuPlayer player = MenuxAPI.playerFactory().create(target);
            final MenuInstance menuInst = menu.createMenu(player);
            menuInst.open();
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
