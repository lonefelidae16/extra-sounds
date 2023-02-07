package dev.stashy.extrasounds.mixin;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public class ItemPickSound
{
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "doItemPick", at = {
            @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;addPickBlock(Lnet/minecraft/item/ItemStack;)V"),
            @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickFromInventory(I)V"),
            @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD)
    },
    locals = LocalCapture.CAPTURE_FAILSOFT)
    void pickSound(CallbackInfo ci, boolean isCreative, BlockEntity blockEntity, ItemStack itemStack)
    {
        if (player != null && !player.getMainHandStack().getItem().equals(itemStack.getItem()))
            SoundManager.playSound(itemStack, SoundType.PICKUP);
    }
}
