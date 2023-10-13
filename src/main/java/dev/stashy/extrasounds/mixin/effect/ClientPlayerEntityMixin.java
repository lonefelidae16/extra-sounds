package dev.stashy.extrasounds.mixin.effect;

import com.mojang.authlib.GameProfile;
import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For player's effect add/remove sound.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    protected void onStatusEffectApplied(StatusEffectInstance effect, @Nullable Entity source) {
        super.onStatusEffectApplied(effect, source);
        if (!effect.shouldShowIcon()) {
            return;
        }
        SoundManager.effectChanged(effect.getEffectType(), SoundManager.EffectType.ADD);
    }

    @Inject(method = "removeStatusEffectInternal", at = @At("HEAD"))
    private void extrasounds$effectRemoved(StatusEffect type, CallbackInfoReturnable<StatusEffectInstance> cir) {
        StatusEffectInstance effect = getActiveStatusEffects().get(type);
        if (effect == null || !effect.shouldShowIcon()) {
            return;
        }
        SoundManager.effectChanged(type, SoundManager.EffectType.REMOVE);
    }
}