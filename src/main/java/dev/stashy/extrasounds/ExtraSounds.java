package dev.stashy.extrasounds;

import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.impl.PrefixableMessageFactory;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExtraSounds implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(
            ExtraSounds.class,
            new PrefixableMessageFactory(ExtraSounds.class.getSimpleName())
    );
    public static final String MODID = "extrasounds";
    public static final SoundEvent MUTED = Sounds.MUTED;

    @Override
    public void onInitializeClient() {
        DebugUtils.init();
        SoundPackLoader.init();
    }

    public static Identifier getClickId(Identifier id, SoundType type) {
        if (id == null || type == null) {
            return MUTED.getId();
        }
        return new Identifier(MODID, "%s.%s.%s".formatted(type.prefix, id.getNamespace(), id.getPath()));
    }

    public static SoundEvent createEvent(String path) {
        try {
            return createEvent(new Identifier(MODID, path));
        } catch (Exception ex) {
            LOGGER.error("Failed to create SoundEvent", ex);
        }
        return MUTED;
    }

    public static SoundEvent createEvent(Identifier id) {
        return SoundEvent.of(id);
    }
}