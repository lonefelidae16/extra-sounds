package dev.stashy.extrasounds.mc1_21_5.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.MinecraftClient;
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

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void extrasounds$hotbarKeySound(CallbackInfo ci, int slot) {
        this.soundHandler.onChange(slot);
    }
}
