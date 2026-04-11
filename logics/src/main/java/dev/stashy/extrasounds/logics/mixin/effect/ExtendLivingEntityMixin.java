package dev.stashy.extrasounds.logics.mixin.effect;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
    public abstract Map<Holder<MobEffect>, MobEffectInstance> getActiveEffectsMap();

    @Inject(method = "onEffectAdded", at = @At("HEAD"))
    protected void extrasounds$invokeOnStatusEffectApplied_AtHead(MobEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        // Empty body for overrideable injection point
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    protected void extrasounds$invokeRemoveStatusEffectInternal_AtHead(Holder<MobEffect> statusEffect, CallbackInfoReturnable<MobEffectInstance> cir) {
        // Empty body for overrideable injection point
    }
}
