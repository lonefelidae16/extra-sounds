package dev.stashy.extrasounds.mc1_20.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * For Hotbar drop action.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @Inject(
            method = "dropSelectedItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void extrasounds$hotbarItemDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir, PlayerActionC2SPacket.Action action, ItemStack itemStack) {
        this.soundHandler.onThrow(itemStack);
    }
}
