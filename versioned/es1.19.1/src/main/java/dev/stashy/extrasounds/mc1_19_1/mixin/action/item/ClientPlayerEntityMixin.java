package dev.stashy.extrasounds.mc1_19_1.mixin.action.item;

import com.mojang.authlib.GameProfile;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Bow pull sound.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Inject(method = "setCurrentHand", at = @At("HEAD"))
    private void extrasounds$bowPullSound(Hand hand, CallbackInfo ci) {
        if (!this.getStackInHand(hand).isOf(Items.BOW)) {
            return;
        }

        ExtraSounds.MANAGER.playSound(Sounds.Actions.BOW_PULL, SoundType.ITEM_INTR);
    }

    @Inject(method = "clearActiveItem", at = @At(value = "HEAD"))
    private void extrasounds$cancelPullSound(CallbackInfo ci) {
        if (!this.activeItemStack.isOf(Items.BOW)) {
            return;
        }

        ExtraSounds.MANAGER.stopSound(Sounds.Actions.BOW_PULL, SoundType.ITEM_INTR);
    }
}
