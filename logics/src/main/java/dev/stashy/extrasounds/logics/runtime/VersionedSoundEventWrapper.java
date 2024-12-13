package dev.stashy.extrasounds.logics.runtime;

import dev.stashy.extrasounds.logics.ExtraSounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;

import java.lang.reflect.Constructor;
import java.util.Objects;

public abstract class VersionedSoundEventWrapper {
    private static final Constructor<VersionedSoundEventWrapper> CTOR_WITH_ID;
    private static final Constructor<VersionedSoundEventWrapper> CTOR_WITH_BLOCK;

    static {
        Constructor<VersionedSoundEventWrapper> ctorId = null;
        Constructor<VersionedSoundEventWrapper> ctorBlock = null;
        try {
            Class<VersionedSoundEventWrapper> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "runtime.SoundEventImpl");
            ctorId = clazz.getConstructor(Identifier.class);
            ctorBlock = clazz.getConstructor(BlockState.class);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Failed to find 'SoundEvent' class.", ex);
        }
        CTOR_WITH_ID = Objects.requireNonNull(ctorId);
        CTOR_WITH_BLOCK = Objects.requireNonNull(ctorBlock);
    }

    public static VersionedSoundEventWrapper newInstance(Identifier id) {
        try {
            return CTOR_WITH_ID.newInstance(id);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'SoundEvent' with class Identifier.", ex);
        }
        return null;
    }

    public static VersionedSoundEventWrapper fromBlockState(BlockState blockState) {
        try {
            return CTOR_WITH_BLOCK.newInstance(blockState);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'SoundEvent' with class BlockState.", ex);
        }
        return null;
    }

    public abstract Object getInstance();

    public abstract Identifier getId();
}
