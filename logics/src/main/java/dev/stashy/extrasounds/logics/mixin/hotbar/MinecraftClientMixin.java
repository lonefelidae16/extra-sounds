package dev.stashy.extrasounds.logics.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Hotbar sound using keyboard.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/SpectatorHud;selectSlot(I)V"))
    private void extrasounds$spectatorHotbarSound(CallbackInfo ci) {
        this.soundHandler.spectatorHotbar();
    }
}
