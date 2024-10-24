package dev.stashy.extrasounds.mc1_21_2.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class SoundEventImpl extends VersionedSoundEventWrapper {
    private final SoundEvent instance;

    public SoundEventImpl(Identifier identifier) {
        this.instance = SoundEvent.of(identifier);
    }

    public SoundEventImpl(BlockState blockState) {
        this.instance = blockState.getSoundGroup().getPlaceSound();
    }

    @Override
    public Object getInstance() {
        return this.instance;
    }

    @Override
    public Identifier getId() {
        return this.instance.id();
    }
}
