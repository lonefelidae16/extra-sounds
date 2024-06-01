package dev.stashy.extrasounds.logics;

import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.mapping.SoundPackLoader;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public final class ExtraSounds {
    public static final Logger LOGGER = LogManager.getLogger(
            ExtraSounds.class,
            new PrefixableMessageFactory(ExtraSounds.class.getSimpleName())
    );
    public static final String MODID = "extrasounds";
    public static final SoundEvent MUTED = Sounds.MUTED;
    public static final VersionedSoundManager MANAGER = Objects.requireNonNull(VersionedSoundManager.newInstance());
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
            return Objects.requireNonNull(Identifier.of(MODID, "%s.%s.%s".formatted(prefix, namespace, path)));
        } catch (Exception ex) {
            LOGGER.error("Failed to create Click Id.", ex);
        }
        return MUTED.getId();
    }

    public static SoundEvent createEvent(String path) {
        try {
            return createEvent(Objects.requireNonNull(Identifier.of(MODID, path)));
        } catch (Exception ex) {
            LOGGER.error("Failed to create SoundEvent.", ex);
        }
        return MUTED;
    }

    public static SoundEvent createEvent(Identifier id) {
        return SoundEvent.of(id);
    }
}