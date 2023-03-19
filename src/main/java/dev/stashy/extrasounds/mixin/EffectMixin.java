package dev.stashy.extrasounds.mixin;

import com.mojang.authlib.GameProfile;
import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class EffectMixin extends AbstractClientPlayerEntity
{

    public EffectMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Override
    public boolean addStatusEffect(StatusEffectInstance effect, @Nullable Entity source)
    {
        var added = super.addStatusEffect(effect, source);
        if (added && !hasEffect(effect.getEffectType()))
            SoundManager.playSound(effect.getEffectType(), true);
        return added;
    }

    @Override
    public void setStatusEffect(StatusEffectInstance effect, @Nullable Entity source)
    {
        if (!hasEffect(effect.getEffectType()))
            SoundManager.playSound(effect.getEffectType(), true);
        super.setStatusEffect(effect, source);
    }

    @Override
    protected void onStatusEffectRemoved(StatusEffectInstance effect)
    {
        if (hasEffect(effect.getEffectType())) SoundManager.playSound(effect.getEffectType(), false);
        super.onStatusEffectRemoved(effect);
    }

    @Inject(at = @At("HEAD"), method = "removeStatusEffectInternal")
    public void removeStatusEffectInternal(StatusEffect type, CallbackInfoReturnable<StatusEffectInstance> cir)
    {
        if (hasStatusEffect(type))
            SoundManager.playSound(type, false);
    }

    private boolean hasEffect(StatusEffect e)
    {
        return getActiveStatusEffects().containsKey(e) && getActiveStatusEffects().get(e).getDuration() > 1;
    }
}