package dev.stashy.extrasounds.mixin.typing;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatInputSuggestor.SuggestionWindow.class)
public abstract class SuggestionWindowMixin {
    @Shadow
    private int selection;
    @Shadow
    private boolean completed;

    @Unique
    private int currentPos;

    @Inject(method = "select", at = @At("RETURN"))
    private void extrasounds$suggestionSelect(int index, CallbackInfo ci) {
        if (this.selection != this.currentPos) {
            SoundManager.keyboard(SoundManager.KeyType.CURSOR);
            this.currentPos = this.selection;
        }
    }

    @Inject(method = "complete", at = @At("HEAD"))
    private void extrasounds$suggestionComplete(CallbackInfo ci) {
        if (this.completed) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.INSERT);
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ChatInputSuggestor;clearWindow()V"))
    private void extrasounds$closeWindow(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        SoundManager.keyboard(SoundManager.KeyType.CURSOR);
    }
}
