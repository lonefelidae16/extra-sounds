package dev.stashy.extrasounds.mixin.inventory;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Creative screen scroll sound.
 */
@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public abstract class CreativeScreenHandlerMixin {
    @Shadow
    protected abstract int getRow(float scroll);

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen$CreativeScreenHandler;scrollItems(F)V"))
    private void extrasounds$creativeScreenCtor(CallbackInfo ci) {
        SoundManager.resetScrollPos();
    }

    @Inject(method = "scrollItems", at = @At("HEAD"))
    private void extrasounds$creativeScreenScroll(float position, CallbackInfo ci) {
        SoundManager.screenScroll(this.getRow(position));
    }
}
