package dev.stashy.extrasounds.mixin.effect;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For player's effect add/remove sound.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends ExtendLivingEntityMixin {
    @Override
    protected void extrasounds$invokeOnStatusEffectApplied_AtHead(StatusEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        super.extrasounds$invokeOnStatusEffectApplied_AtHead(effect, source, ci);
        if (!effect.shouldShowIcon()) {
            return;
        }
        SoundManager.effectChanged(effect.getEffectType().value(), SoundManager.EffectType.ADD);
    }

    @Inject(method = "removeStatusEffectInternal", at = @At("HEAD"))
    private void extrasounds$effectRemoved(RegistryEntry<StatusEffect> registryEntry, CallbackInfoReturnable<StatusEffectInstance> cir) {
        StatusEffectInstance effect = getActiveStatusEffects().get(registryEntry);
        if (effect == null || !effect.shouldShowIcon()) {
            return;
        }
        SoundManager.effectChanged(effect.getEffectType().value(), SoundManager.EffectType.REMOVE);
    }
}