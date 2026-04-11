package dev.stashy.extrasounds.logics.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.font.TextFieldHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(TextFieldHelper.class)
public abstract class TextFieldHelperMixin {
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();
    @Unique
    private boolean bPasteAction = false;

    @Unique
    private static final String METHOD_SIGN_DELETE = "removeCharsFromCursor(I)V";

    @Shadow
    private int cursorPos;
    @Shadow
    private int selectionPos;
    @Shadow
    private @Final Supplier<String> getMessageFn;

    @Inject(method = METHOD_SIGN_DELETE, at = @At("HEAD"))
    private void extrasounds$beforeDelete(int count, CallbackInfo ci) {
        final String text = this.getMessageFn.get();
        this.soundHandler.onCharErase(count, text.length(), this.cursorPos, this.selectionPos);
    }

    @Inject(method = METHOD_SIGN_DELETE, at = @At("RETURN"))
    private void extrasounds$afterDelete(CallbackInfo ci) {
        this.soundHandler.setCursor(this.selectionPos);
    }

    @Inject(method = "cut", at = @At("HEAD"))
    private void extrasounds$cutAction(CallbackInfo ci) {
        if (this.cursorPos == this.selectionPos) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.CUT);
    }

    @Inject(method = "cut", at = @At("RETURN"))
    private void extrasounds$afterCut(CallbackInfo ci) {
        this.soundHandler.setCursor(this.selectionPos);
    }

    @Inject(method = "insertText(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"))
    private void extrasounds$appendChar(String string, String insertion, CallbackInfo ci) {
        if (!this.soundHandler.isPosUpdated(this.cursorPos, this.selectionPos)) {
            return;
        }
        if (this.bPasteAction) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.PASTE);
            this.bPasteAction = false;
        } else if (insertion.equals("\n")) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.RETURN);
        } else {
            this.soundHandler.onKey(TextFieldHandler.KeyType.INSERT);
        }
        this.soundHandler.setCursor(this.selectionPos);
    }

    @Inject(method = "paste", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/font/TextFieldHelper;insertText(Ljava/lang/String;Ljava/lang/String;)V"))
    private void extrasounds$pasteAction(CallbackInfo ci) {
        this.bPasteAction = true;
    }

    @Inject(method = "resetSelectionIfNeeded(Z)V", at = @At("RETURN"))
    private void extrasounds$moveCursor(boolean shiftDown, CallbackInfo ci) {
        if (!this.soundHandler.isPosUpdated(this.cursorPos, this.selectionPos)) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
        this.soundHandler.setCursorStart(this.cursorPos);
        this.soundHandler.setCursorEnd(this.selectionPos);
    }
}
