package dev.stashy.extrasounds.logics.sounds;

import dev.stashy.extrasounds.logics.ExtraSounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;

import java.lang.reflect.Method;

public interface VersionedSoundWrapper {
    static VersionedSoundWrapper newInstance(Identifier id, float volume, float pitch, int weight, Sound.RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        try {
            Class<VersionedSoundWrapper> instance = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "runtime.SoundImpl");
            Method init = instance.getMethod("init", Identifier.class, float.class, float.class, int.class, Sound.RegistrationType.class, boolean.class, boolean.class, int.class);
            return (VersionedSoundWrapper) init.invoke(null, id, volume, pitch, weight, registrationType, stream, preload, attenuation);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Failed to initialize 'Sound'", ex);
        }
        return null;
    }
}
