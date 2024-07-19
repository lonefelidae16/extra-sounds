package dev.stashy.extrasounds.logics.mixin.hotbar;

import dev.stashy.extrasounds.logics.impl.HotbarSoundHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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

    @Unique
    private final HotbarSoundHandler soundHandler = new HotbarSoundHandler();

    @Inject(method = "handleInputEvents", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", shift = At.Shift.AFTER))
    private void extrasounds$hotbarKeySound(CallbackInfo ci) {
        if (this.player != null) {
            this.soundHandler.onChange();
        }
    }

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;selectSlot(I)V"))
    private void extrasounds$spectatorHotbarSound(CallbackInfo ci) {
        this.soundHandler.spectatorHotbar();
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
        if (player != null) {
            this.soundHandler.onItemPick(itemStack.getItem());
        }
    }
}
