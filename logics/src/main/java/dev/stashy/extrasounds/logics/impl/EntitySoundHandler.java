package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

/**
 * Helper class for managing {@link net.minecraft.entity.Entity} status.
 */
public class EntitySoundHandler {
    public enum EffectType {
        ADD,
        REMOVE
    }

    public void onEffectChanged(StatusEffect effect, EffectType type) {
        if (DebugUtils.DEBUG) {
            ExtraSounds.LOGGER.info("EffectType = {}, Effect = {}", type, effect.getName().getString());
        }

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
            ExtraSounds.LOGGER.error("Argument of type '{}' is not supported for '{}'", EffectType.class.getSimpleName(), type);
            return;
        }

        ExtraSounds.MANAGER.playSound(sound, SoundType.EFFECTS);
    }

    public void onDeath(Entity entity, BlockPos blockPos) {
        final float flu = (float) ((Math.random() - 0.5f) * 0.333333f);
        final float pitch = flu + (float) MathHelper.clampedLerp(2f, 0.5f, Math.sqrt(entity.getHeight() * entity.getWidth()) * 0.4f);
        ExtraSounds.MANAGER.playSound(Sounds.Entities.POOF, SoundType.ENTITY, .7f, pitch, blockPos);
    }

    public void onItemUse(Item item) {
        if (item == Items.AIR) {
            return;
        }

        ExtraSounds.MANAGER.playSound(ExtraSounds.MANAGER.getSoundByItem(item, SoundType.PICKUP), SoundType.ENTITY);
    }
}
