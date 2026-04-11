package dev.stashy.extrasounds.logics.mixin.effect;

import dev.stashy.extrasounds.logics.impl.EntitySoundHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For player's effect add/remove sound.
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends ExtendLivingEntityMixin {
    @Unique
    private final EntitySoundHandler soundHandler = new EntitySoundHandler();

    @Override
    protected void extrasounds$invokeOnStatusEffectApplied_AtHead(MobEffectInstance effect, @Nullable Entity source, CallbackInfo ci) {
        super.extrasounds$invokeOnStatusEffectApplied_AtHead(effect, source, ci);
        if (!effect.showIcon() || effect.endsWithin(1)) {
            return;
        }
        this.soundHandler.onEffectChanged(effect.getEffect().value(), EntitySoundHandler.EffectType.ADD);
    }

    @Override
    protected void extrasounds$invokeRemoveStatusEffectInternal_AtHead(Holder<MobEffect> statusEffect, CallbackInfoReturnable<MobEffectInstance> cir) {
        super.extrasounds$invokeRemoveStatusEffectInternal_AtHead(statusEffect, cir);

        MobEffectInstance effect = getActiveEffectsMap().get(statusEffect);
        if (effect == null || !effect.showIcon()) {
            return;
        }
        this.soundHandler.onEffectChanged(effect.getEffect().value(), EntitySoundHandler.EffectType.REMOVE);
    }
}