package dev.stashy.extrasounds.mixin.action;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
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

        final float flu = (this.random.nextFloat() - 0.5f) * 0.333333f;
        final float pitch = flu + (float) MathHelper.clampedLerp(2f, 0.5f,  Math.sqrt(this.getHeight() * this.getWidth()) * 0.4f);
        SoundManager.playSound(Sounds.Entities.POOF, SoundType.ACTION, .7f, pitch, this.getBlockPos());
    }
}
