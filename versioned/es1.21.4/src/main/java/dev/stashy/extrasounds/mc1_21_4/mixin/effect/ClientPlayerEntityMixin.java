package dev.stashy.extrasounds.mc1_21_4.mixin.effect;

import dev.stashy.extrasounds.logics.impl.EntitySoundHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For player's effect add/remove sound.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends ExtendLivingEntityMixin {
    @Unique
    private final EntitySoundHandler soundHandler = new EntitySoundHandler();

    @Override
    protected void extrasounds$invokeOnStatusEffectApplied_AtHead(StatusEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        super.extrasounds$invokeOnStatusEffectApplied_AtHead(effect, source, ci);
        if (!effect.shouldShowIcon() || effect.isDurationBelow(1)) {
            return;
        }
        this.soundHandler.onEffectChanged(effect.getEffectType().value(), EntitySoundHandler.EffectType.ADD);
    }

    @Override
    protected void extrasounds$invokeRemoveStatusEffectInternal_AtHead(RegistryEntry<StatusEffect> statusEffect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        super.extrasounds$invokeRemoveStatusEffectInternal_AtHead(statusEffect, cir);

        StatusEffectInstance effect = getActiveStatusEffects().get(statusEffect);
        if (effect == null || !effect.shouldShowIcon()) {
            return;
        }
        this.soundHandler.onEffectChanged(effect.getEffectType().value(), EntitySoundHandler.EffectType.REMOVE);
    }
}