package dev.stashy.extrasounds.debug;

import dev.stashy.extrasounds.mapping.SoundGenerator;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugUtils {
    public static final String debugVar = "extrasounds.debug";
    public static final String debugPathVar = "extrasounds.debug.path";
    public static final String noCacheVar = "extrasounds.nocache";
    private static final String JVM_ARG_SEARCH_UNDEF_SND = "extrasounds.searchundef";

    public static final boolean debug = System.getProperties().containsKey(debugVar)
            && System.getProperty(debugVar).equals("true");
    public static final String debugPath = System.getProperties().containsKey(debugPathVar)
            ? System.getProperty(debugPathVar) : "debug/";
    public static final boolean noCache = System.getProperties().containsKey(noCacheVar)
            && System.getProperties().get(noCacheVar).equals("true");
    /**
     * For debugging.<br>
     * When run with a JVM argument {@link DebugUtils#JVM_ARG_SEARCH_UNDEF_SND}, the log shows a SoundEntry that plays the default {@link dev.stashy.extrasounds.sounds.Sounds#ITEM_PICK}.<br>
     * To ensure that the debugging statements are executed, it is recommended that you also run with the {@link DebugUtils#noCacheVar} JVM argument.
     */
    public static final boolean SEARCH_UNDEF_SOUND = System.getProperties().containsKey(JVM_ARG_SEARCH_UNDEF_SND)
            && System.getProperties().get(JVM_ARG_SEARCH_UNDEF_SND).equals("true");

    private static final Logger LOGGER = LogManager.getLogger();

    public static void init() {
        if (!debug) return;
        LOGGER.info("ExtraSounds: DEBUG mode enabled.");
        LOGGER.info("Debug path: " + Path.of(debugPath).toAbsolutePath());
    }

    public static void exportSoundsJson(byte[] jsonData) {
        if (!debug) return;
        try {
            Path p = Path.of(debugPath).resolve("sounds.json");
            createFile(p);
            Files.write(p, jsonData, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void exportGenerators(Map<String, SoundGenerator> generator) {
        if (!debug) return;
        Path p = Path.of(debugPath).resolve("generators.txt");
        createFile(p);
        try {
            Files.write(p, generator.keySet().stream()
                    .map(it -> {
                        var clazz = generator.get(it).itemSoundGenerator.getClass();
                        return "namespace: " + it + "; class: " + (clazz == null ? "none" : clazz.getName());
                    })
                    .collect(Collectors.toList()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void soundLog(SoundEvent snd) {
        if (!debug) return;
        LOGGER.info("Playing sound: " + snd.getId());
    }

    public static void effectLog(StatusEffect effect, boolean add) {
        if (!debug) return;
        boolean positive = !effect.getCategory().equals(StatusEffectCategory.HARMFUL);
        LOGGER.info(
                (positive ? "Positive" : "Negative") + " effect " + (add ? "added" : "removed") + ": " + effect.getName()
                        .getString());
    }

    public static void genericLog(String message) {
        if (!debug) return;
        LOGGER.info(message);
    }

    private static void createFile(Path p) {
        try {
            final Path debugPath = Path.of(DebugUtils.debugPath);
            if (!Files.isDirectory(debugPath))
                Files.createDirectory(debugPath);
            if (!Files.exists(p))
                Files.createFile(p);
        } catch (IOException e) {
            LOGGER.error("Unable to create file: " + p);
            e.printStackTrace();
        }
    }
}
