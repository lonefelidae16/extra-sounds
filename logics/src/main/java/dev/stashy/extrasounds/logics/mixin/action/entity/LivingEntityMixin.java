package dev.stashy.extrasounds.logics.mixin.action.entity;

import dev.stashy.extrasounds.logics.impl.EntitySoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private final EntitySoundHandler soundHandler = new EntitySoundHandler();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "addDeathParticles", at = @At("HEAD"))
    private void extrasounds$poofSound(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            return;
        }

        this.soundHandler.onDeath(LivingEntity.class.cast(this), this.getBlockPos());
    }
}
