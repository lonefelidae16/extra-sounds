package dev.stashy.extrasounds;

import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class ExtraSounds implements ClientModInitializer {
    public static final String MODID = "extrasounds";
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
}