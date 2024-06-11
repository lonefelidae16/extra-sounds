package dev.stashy.extrasounds.logics;

import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.entry.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ExtraSounds {
    public static final Logger LOGGER = LogManager.getLogger(
            ExtraSounds.class,
            new PrefixableMessageFactory(ExtraSounds.class.getSimpleName())
    );

    public static final Map<String, Method> CACHED_METHOD_MAP = new HashMap<>();
    private static final VersionedMain MAIN = Objects.requireNonNull(VersionedMain.newInstance());

    public static final String MODID = "extrasounds";
    public static final SoundManager MANAGER = new SoundManager();
    public static final String BASE_PACKAGE = "dev.stashy.extrasounds";

    public static void init() {
        DebugUtils.init();
        SoundPackLoader.init();
    }

    public static Identifier getClickId(Identifier id, SoundType type) {
        try {
            final String prefix = type.prefix;
            final String namespace = id.getNamespace();
            final String path = id.getPath();
            if (prefix.isBlank() || namespace.isBlank() || path.isBlank()) {
                throw new IllegalArgumentException(
                        "Identifier cannot contain blank String: prefix = '%s', namespace = '%s', path = '%s'".formatted(
                                prefix,
                                namespace,
                                path
                        )
                );
            }
            return Objects.requireNonNull(MAIN.generateIdentifier(ExtraSounds.MODID, "%s.%s.%s".formatted(prefix, namespace, path)));
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Failed to create Click Id.", ex);
        }
        return Sounds.MUTED.getId();
    }

    public static SoundEvent createEvent(String path) {
        return createEvent(Objects.requireNonNull(generateIdentifier(MODID, path)));
    }

    public static SoundEvent createEvent(Identifier id) {
        return MAIN.generateSoundEvent(id);
    }

    public static Identifier generateIdentifier(String path) {
        return generateIdentifier(MODID, path);
    }

    public static Identifier generateIdentifier(String namespace, String path) {
        return MAIN.generateIdentifier(namespace, path);
    }

    public static Identifier fromItemRegistry(Item item) {
        return MAIN.fromItemRegistry(item);
    }

    public static IndexedIterable<Item> getItemRegistry() {
        return MAIN.getItemRegistry();
    }

    public static boolean canItemsCombine(ItemStack stack1, ItemStack stack2) {
        return MAIN.canItemsCombine(stack1, stack2);
    }
}