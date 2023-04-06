package dev.stashy.extrasounds;

import com.mojang.logging.LogUtils;
import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class ExtraSounds implements ClientModInitializer {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "extrasounds";
    public static final SoundEvent MISSING = SoundEvent.of(new Identifier(MODID, "missing"));
    static final Random mcRandom = Random.create();

    @Override
    public void onInitializeClient() {
        DebugUtils.init();
        SoundPackLoader.init();
    }

    @Nullable
    public static Identifier getClickId(Identifier id, SoundType type) {
        if (id == null || type == null) {
            return null;
        }
        return new Identifier(MODID, "%s.%s.%s".formatted(type.prefix, id.getNamespace(), id.getPath()));
    }

    public static SoundEvent createEvent(String path) {
        try {
            return SoundEvent.of(new Identifier(MODID, path));
        } catch (Throwable ex) {
            LOGGER.error("[%s] Failed to create SoundEvent".formatted(ExtraSounds.class.getSimpleName()), ex);
        }
        return MISSING;
    }
}