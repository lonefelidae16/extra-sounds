package dev.stashy.extrasounds.mc1_19.runtime;

import dev.stashy.extrasounds.logics.sounds.VersionedPositionedSoundInstanceWrapper;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class PositionedSoundInstanceImpl extends PositionedSoundInstance implements VersionedPositionedSoundInstanceWrapper {
    private static final Random MC_RANDOM = Random.create();

    public PositionedSoundInstanceImpl(Identifier id, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, double x, double y, double z, boolean relative) {
        super(id, category, volume, pitch, MC_RANDOM, repeat, repeatDelay, attenuationType, x, y, z, relative);
    }

    public static PositionedSoundInstanceImpl init(Identifier id, SoundCategory category, float volume, float pitch, boolean repeat, int repeatDelay, AttenuationType attenuationType, double x, double y, double z, boolean relative) {
        return new PositionedSoundInstanceImpl(id, category, volume, pitch, repeat, repeatDelay, attenuationType, x, y, z, relative);
    }
}
