package dev.stashy.extrasounds.mc1_21.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

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
        this.soundHandler.doItemPick(itemStack.getItem());
    }

    @Inject(method = "handleInputEvents", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void extrasounds$hotbarKeySound(CallbackInfo ci, int slot) {
        this.soundHandler.onChange(slot);
    }
}
