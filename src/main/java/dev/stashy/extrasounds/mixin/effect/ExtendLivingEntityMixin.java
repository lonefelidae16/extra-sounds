package dev.stashy.extrasounds.mixin.effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class ExtendLivingEntityMixin {
    @Shadow
    public abstract Map<StatusEffect, StatusEffectInstance> getActiveStatusEffects();

    @Inject(method = "onStatusEffectApplied", at = @At("HEAD"))
    protected void extrasounds$invokeOnStatusEffectApplied_AtHead(StatusEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        // Empty body for overrideable injection point
    }
}
