package dev.stashy.extrasounds.logics.sounds;

import dev.stashy.extrasounds.logics.ExtraSounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;
import java.util.Objects;

public interface VersionedSoundWrapper {
    String METHOD_SIGNATURE = VersionedSoundWrapper.class.getCanonicalName() + "#init";

    static VersionedSoundWrapper newInstance(Identifier id, float volume, float pitch, int weight, Sound.RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        Method init = ExtraSounds.CACHED_METHOD_MAP.getOrDefault(METHOD_SIGNATURE, null);

        if (init == null) {
            try {
                Class<VersionedSoundWrapper> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "runtime.SoundImpl");
                init = Objects.requireNonNull(clazz).getMethod("init", Identifier.class, float.class, float.class, int.class, Sound.RegistrationType.class, boolean.class, boolean.class, int.class);
                ExtraSounds.CACHED_METHOD_MAP.put(METHOD_SIGNATURE, Objects.requireNonNull(init));
            } catch (Exception ex) {
                ExtraSounds.LOGGER.error("Failed to find 'Sound' class.", ex);
            }
        }

        try {
            return (VersionedSoundWrapper) Objects.requireNonNull(init).invoke(null, id, volume, pitch, weight, registrationType, stream, preload, attenuation);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'Sound'", ex);
        }

        return null;
    }


    Identifier getIdentifierImpl();

    Object getVolumeImpl();

    Object getPitchImpl();

    int getWeightImpl();

    Sound.RegistrationType getRegistrationTypeImpl();

    boolean isStreamedImpl();

    boolean isPreloadedImpl();

    int getAttenuationImpl();
}
