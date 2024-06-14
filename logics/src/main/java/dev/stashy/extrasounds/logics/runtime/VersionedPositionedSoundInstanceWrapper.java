package dev.stashy.extrasounds.logics.runtime;

import dev.stashy.extrasounds.logics.ExtraSounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Method;
import java.util.Objects;

public interface VersionedPositionedSoundInstanceWrapper extends SoundInstance {
    String METHOD_KEY_INIT = VersionedPositionedSoundInstanceWrapper.class.getCanonicalName() + "#init";

    static VersionedPositionedSoundInstanceWrapper newInstance(Identifier id, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, SoundInstance.AttenuationType attenuationType, double x, double y, double z, boolean relative) {
        Method init = ExtraSounds.CACHED_METHOD_MAP.getOrDefault(METHOD_KEY_INIT, null);

        if (init == null) {
            try {
                Class<VersionedPositionedSoundInstanceWrapper> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "runtime.PositionedSoundInstanceImpl");
                init = clazz.getMethod("init", Identifier.class, SoundCategory.class, float.class, float.class, boolean.class, int.class, SoundInstance.AttenuationType.class, double.class, double.class, double.class, boolean.class);
                ExtraSounds.CACHED_METHOD_MAP.put(METHOD_KEY_INIT, Objects.requireNonNull(init));
            } catch (Exception ex) {
                ExtraSounds.LOGGER.error("Failed to find 'PositionedSoundInstance' class.", ex);
            }
        }

        try {
            return (VersionedPositionedSoundInstanceWrapper) Objects.requireNonNull(init).invoke(null, id, category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, relative);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot instantiate 'PositionedSoundInstance'", ex);
        }

        return null;
    }

    static VersionedPositionedSoundInstanceWrapper newInstance(SoundEvent soundEvent, SoundCategory category, float volume, float pitch, BlockPos position) {
        return newInstance(soundEvent.getId(), category, volume, pitch, false, 0, SoundInstance.AttenuationType.LINEAR, position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, false);
    }
}
