package dev.stashy.extrasounds.mc1_18_2.runtime;

import dev.stashy.extrasounds.logics.sounds.VersionedPositionedSoundInstanceWrapper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class PositionedSoundInstanceImpl extends PositionedSoundInstance implements VersionedPositionedSoundInstanceWrapper {
    public PositionedSoundInstanceImpl(Identifier id, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, double x, double y, double z, boolean relative) {
        super(id, category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, relative);
    }

    public static PositionedSoundInstanceImpl init(Identifier id, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, double x, double y, double z, boolean relative) {
        return new PositionedSoundInstanceImpl(id, category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, relative);
    }
}
