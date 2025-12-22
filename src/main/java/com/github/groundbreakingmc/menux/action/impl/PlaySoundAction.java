package com.github.groundbreakingmc.menux.action.impl;

import com.github.groundbreakingmc.menux.action.ActionCreationContext;
import com.github.groundbreakingmc.menux.action.MenuAction;
import com.github.groundbreakingmc.menux.exception.ActionCreateException;
import com.github.groundbreakingmc.menux.menu.context.MenuContext;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Action that plays a sound to the player.
 * Supports customization of volume, pitch, source, and seed.
 */
public final class PlaySoundAction implements MenuAction {

    private final Sound sound;

    public PlaySoundAction(Sound sound) {
        this.sound = sound;
    }

    @Override
    public void run(@NotNull MenuContext context) {
        context.player().playSound(this.sound);
    }

    public static class Factory implements MenuAction.Factory {
        /**
         * Creates a new PlaySoundAction instance from configuration data.
         *
         * <p>Required parameters:
         * <ul>
         *   <li>{@code key} (String) - the sound key (e.g., "minecraft:entity.player.levelup")</li>
         * </ul>
         *
         * <p>Optional parameters:
         * <ul>
         *   <li>{@code source} (String) - the sound source category (default: MASTER)</li>
         *   <li>{@code volume} (Number) - the sound volume (default: 1.0)</li>
         *   <li>{@code pitch} (Number) - the sound pitch (default: 1.0)</li>
         *   <li>{@code seed} (Number) - the random seed for sound variation</li>
         * </ul>
         *
         * @param context the creation context (unused for this action)
         * @param rawData the raw configuration data containing sound parameters
         * @return a new PlaySoundAction instance
         * @throws ActionCreateException if required parameters are missing or invalid
         */
        @Override
        public @NotNull MenuAction create(@NotNull ActionCreationContext context, @NotNull Map<String, Object> rawData)
                throws ActionCreateException {

            final Sound.Builder builder = Sound.sound();
            setSound(rawData, builder);
            setSource(rawData, builder);
            setVolume(rawData, builder);
            setPitch(rawData, builder);
            setSeed(rawData, builder);
            return new PlaySoundAction(builder.build());
        }

        private static void setSound(Map<String, Object> entries, Sound.Builder builder) throws ActionCreateException {
            final Object raw = entries.get("key");
            if (raw == null) {
                throw new ActionCreateException("Missing required parameter 'key' for sound");
            }
            @Subst("minecraft:ambient.basalt_deltas.additions") final String key = String.valueOf(raw);
            builder.type(Key.key(key));
        }

        private static void setSource(Map<String, Object> entries, Sound.Builder builder) throws ActionCreateException {
            final Object source = entries.get("source");
            if (source != null) {
                try {
                    builder.source(Sound.Source.valueOf(String.valueOf(source).toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ActionCreateException("Invalid sound source: " + source + ". Valid values: " +
                            String.join(", ", getSoundSourceNames()));
                }
            }
        }

        private static void setVolume(Map<String, Object> entries, Sound.Builder builder) {
            final float volume = ((Number) entries.getOrDefault("volume", 1f)).floatValue();
            builder.volume(volume);
        }

        private static void setPitch(Map<String, Object> entries, Sound.Builder builder) {
            final float pitch = ((Number) entries.getOrDefault("pitch", 1f)).floatValue();
            builder.pitch(pitch);
        }

        private static void setSeed(Map<String, Object> entries, Sound.Builder builder) {
            final Object seed = entries.get("seed");
            if (seed != null) {
                builder.seed(((Number) seed).longValue());
            }
        }

        private static String[] getSoundSourceNames() {
            final Sound.Source[] sources = Sound.Source.values();
            final String[] names = new String[sources.length];
            for (int i = 0; i < sources.length; i++) {
                names[i] = sources[i].name();
            }
            return names;
        }
    }
}
