package dev.stashy.extrasounds.logics.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.screens.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatScreen.class)
public abstract class ChatScreenMixin {
    @Shadow
    private int historyPos;

    @Unique
    private int currentHistoryPos;
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();

    @Unique
    private void extrasounds$updateHistoryPos() {
        this.currentHistoryPos = this.historyPos;
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void extrasounds$getDefaultPos(CallbackInfo ci) {
        this.extrasounds$updateHistoryPos();
    }

    @Inject(method = "moveInHistory", at = @At("RETURN"))
    private void extrasounds$selectHistory(int offset, CallbackInfo ci) {
        if (this.historyPos != this.currentHistoryPos) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
            this.extrasounds$updateHistoryPos();
        }
    }
}
