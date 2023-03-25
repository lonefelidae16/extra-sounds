package dev.stashy.extrasounds.mixin.hotbar;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
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

/**
 * For Hotbar action includes keyboard, item pick.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @Inject(method = "handleInputEvents", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void extrasounds$hotbarKeySound(CallbackInfo ci, int i) {
        if (this.player != null && this.player.getInventory().selectedSlot != i) {
            SoundManager.hotbar(i);
        }
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;selectSlot(I)V"))
    private void extrasounds$spectatorHotbarSound(CallbackInfo ci) {
        SoundManager.playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
    }

    @Inject(
            method = "doItemPick",
            at = {
                    @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;addPickBlock(Lnet/minecraft/item/ItemStack;)V"),
                    @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;pickFromInventory(I)V"),
                    @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD)
            },
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void extrasounds$itemPickSound(CallbackInfo ci, boolean isCreative, BlockEntity blockEntity, ItemStack itemStack) {
        if (player != null && !player.getMainHandStack().getItem().equals(itemStack.getItem())) {
            SoundManager.playSound(itemStack, SoundType.PICKUP);
        }
    }
}
