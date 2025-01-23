package dev.stashy.extrasounds.mc1_21_4.mixin.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class ExtendLivingEntityMixin {
    @Shadow
    public abstract Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects();

    @Inject(method = "onStatusEffectApplied", at = @At("HEAD"))
    protected void extrasounds$invokeOnStatusEffectApplied_AtHead(StatusEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        // Empty body for overrideable injection point
    }

    @Inject(method = "removeStatusEffectInternal", at = @At("HEAD"))
    protected void extrasounds$invokeRemoveStatusEffectInternal_AtHead(RegistryEntry<StatusEffect> statusEffect, CallbackInfoReturnable<StatusEffectInstance> cir) {
        // Empty body for overrideable injection point
    }
}
