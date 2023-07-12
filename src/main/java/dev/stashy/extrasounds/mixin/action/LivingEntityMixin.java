package dev.stashy.extrasounds.mixin.action;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "addDeathParticles", at = @At("HEAD"))
    private void extrasounds$poofSound(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            return;
        }

        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        final float pitch = (float) MathHelper.clampedLerp(2f, 0.25f,  Math.sqrt(this.getHeight() * this.getWidth()) / 2.5f);
        SoundManager.playSound(Sounds.Entities.POOF, SoundType.ACTION, .7f, pitch, this.getBlockPos());
    }
}
