package dev.stashy.extrasounds.mixin.action.item;

import com.mojang.authlib.GameProfile;
import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Bow pull sound.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "setCurrentHand", at = @At("HEAD"))
    private void extrasounds$bowPullSound(Hand hand, CallbackInfo ci) {
        if (!this.getStackInHand(hand).isOf(Items.BOW)) {
            return;
        }

        SoundManager.playSound(Sounds.Actions.BOW_PULL, SoundType.ITEM_INTR);
    }

    @Inject(method = "clearActiveItem", at = @At(value = "HEAD"))
    private void extrasounds$cancelPullSound(CallbackInfo ci) {
        if (!this.activeItemStack.isOf(Items.BOW)) {
            return;
        }

        SoundManager.stopSound(Sounds.Actions.BOW_PULL, SoundType.ITEM_INTR);
    }
}
