package dev.stashy.extrasounds.mc1_19_2.runtime;

import dev.stashy.extrasounds.logics.sounds.VersionedSoundWrapper;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;
import net.minecraft.util.math.floatprovider.FloatSupplier;

public class SoundImpl extends Sound implements VersionedSoundWrapper {
    public SoundImpl(String id, FloatSupplier volume, FloatSupplier pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        super(id, volume, pitch, weight, registrationType, stream, preload, attenuation);
    }

    public static SoundImpl init(Identifier id, float volume, float pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        return new SoundImpl(id.toString(), ConstantFloatProvider.create(volume), ConstantFloatProvider.create(pitch), weight, registrationType, stream, preload, attenuation);
    }
}
