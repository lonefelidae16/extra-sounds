package dev.stashy.extrasounds.mc1_20.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow
    private int messageHistorySize;

    @Unique
    private int currentHistoryPos;
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();

    @Unique
    private void extrasounds$updateHistoryPos() {
        this.currentHistoryPos = this.messageHistorySize;
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void extrasounds$getDefaultPos(CallbackInfo ci) {
        this.extrasounds$updateHistoryPos();
    }

    @Inject(method = "setChatFromHistory", at = @At("RETURN"))
    private void extrasounds$selectHistory(int offset, CallbackInfo ci) {
        if (this.messageHistorySize != this.currentHistoryPos) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
            this.extrasounds$updateHistoryPos();
        }
    }
}
