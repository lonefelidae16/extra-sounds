package dev.stashy.extrasounds.impl;

import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.sound.SoundEvent;

/**
 * Helper class for managing {@link net.minecraft.entity.Entity} status.
 */
public class EntitySoundHandler {
    public enum EffectType {
        ADD,
        REMOVE
    }

    public void onEffectChanged(StatusEffect effect, EffectType type) {
        DebugUtils.effectLog(effect, type);

        final SoundEvent sound;
        if (type == EffectType.ADD) {
            sound = switch (effect.getCategory()) {
                case HARMFUL -> Sounds.EFFECT_ADD_NEGATIVE;
                case NEUTRAL, BENEFICIAL -> Sounds.EFFECT_ADD_POSITIVE;
            };
        } else if (type == EffectType.REMOVE) {
            sound = switch (effect.getCategory()) {
                case HARMFUL -> Sounds.EFFECT_REMOVE_NEGATIVE;
                case NEUTRAL, BENEFICIAL -> Sounds.EFFECT_REMOVE_POSITIVE;
            };
        } else {
            ExtraSounds.LOGGER.error("Unknown type of '{}' is approaching: '{}'", EffectType.class.getSimpleName(), type);
            return;
        }

        ExtraSounds.MANAGER.playSound(sound, SoundType.EFFECTS);
    }
}
