package dev.stashy.extrasounds;

import com.mojang.logging.LogUtils;
import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

public class ExtraSounds implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "extrasounds";
    public static final Identifier MISSING = new Identifier(MODID, "missing");

    @Override
    public void onInitializeClient() {
        DebugUtils.init();
        SoundPackLoader.init();
    }

    public static Identifier getClickId(Identifier id, SoundType type) {
        if (id == null || type == null) {
            return MISSING;
        }
        return new Identifier(MODID, "%s.%s.%s".formatted(type.prefix, id.getNamespace(), id.getPath()));
    }

    public static SoundEvent createEvent(String path) {
        try {
            return createEvent(new Identifier(MODID, path));
        } catch (Exception ex) {
            LOGGER.error("[%s] Failed to create SoundEvent".formatted(ExtraSounds.class.getSimpleName()), ex);
        }
        return createEvent(MISSING);
    }

    public static SoundEvent createEvent(Identifier id) {
        return SoundEvent.of(id);
    }
}