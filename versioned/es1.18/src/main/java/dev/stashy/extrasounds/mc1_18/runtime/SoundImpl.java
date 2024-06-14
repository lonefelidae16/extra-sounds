package dev.stashy.extrasounds.mc1_18.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedSoundWrapper;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.Identifier;

public class SoundImpl extends Sound implements VersionedSoundWrapper {
    public SoundImpl(String id, float volume, float pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        super(id, volume, pitch, weight, registrationType, stream, preload, attenuation);
    }

    public static SoundImpl init(Identifier id, float volume, float pitch, int weight, RegistrationType registrationType, boolean stream, boolean preload, int attenuation) {
        return new SoundImpl(id.toString(), volume, pitch, weight, registrationType, stream, preload, attenuation);
    }

    @Override
    public Identifier getIdentifierImpl() {
        return this.getIdentifier();
    }

    @Override
    public Object getVolumeImpl() {
        return this.getVolume();
    }

    @Override
    public Object getPitchImpl() {
        return this.getPitch();
    }

    @Override
    public int getWeightImpl() {
        return this.getWeight();
    }

    @Override
    public RegistrationType getRegistrationTypeImpl() {
        return this.getRegistrationType();
    }

    @Override
    public boolean isStreamedImpl() {
        return this.isStreamed();
    }

    @Override
    public boolean isPreloadedImpl() {
        return this.isPreloaded();
    }

    @Override
    public int getAttenuationImpl() {
        return this.getAttenuation();
    }
}
