package dev.stashy.extrasounds.mixin.hotbar;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ClientPlayerEntity.class)
public class HotbarDropSound
{
    @Inject(at = @At("TAIL"), method = "dropSelectedItem", locals = LocalCapture.CAPTURE_FAILSOFT)
    private void dropItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir, PlayerActionC2SPacket.Action action, ItemStack itemStack)
    {
        if (!itemStack.isEmpty())
        {
            SoundManager.playThrow(itemStack);
        }
    }
}
