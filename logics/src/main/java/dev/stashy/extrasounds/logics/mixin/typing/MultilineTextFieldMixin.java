package dev.stashy.extrasounds.logics.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultilineTextField.class)
public abstract class MultilineTextFieldMixin {
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();
    @Unique
    private boolean bPasteAction = false;
    @Unique
    private boolean bCutAction = false;

    @Shadow
    private int cursor;
    @Shadow
    private int selectCursor;
    @Shadow
    private String value;
    @Shadow
    public abstract boolean hasSelection();

    @Inject(method = "deleteText(I)V", at = @At("HEAD"))
    private void extrasounds$delete(int offset, CallbackInfo ci) {
        this.soundHandler.onCharErase(offset, this.value.length(), this.cursor, this.selectCursor);
    }

    @Inject(method = "insertText(Ljava/lang/String;)V", at = @At("HEAD"))
    private void extrasounds$replaceSelection(String replacement, CallbackInfo ci) {
        if (!replacement.isEmpty()) {
            if (this.bPasteAction) {
                this.soundHandler.onKey(TextFieldHandler.KeyType.PASTE);
                this.bPasteAction = false;
            } else if (replacement.equals("\n")) {
                this.soundHandler.onKey(TextFieldHandler.KeyType.RETURN);
            } else {
                this.soundHandler.onKey(TextFieldHandler.KeyType.INSERT);
            }
        } else if (this.bCutAction && this.hasSelection()) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.CUT);
            this.bCutAction = false;
        }
        this.soundHandler.setCursor(this.selectCursor);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"))
    private void extrasounds$specialAction(KeyEvent keyInput, CallbackInfoReturnable<Boolean> cir) {
        this.bPasteAction = keyInput.isPaste();
        this.bCutAction = keyInput.isCut();
    }

    @Inject(method = "seekCursor(Lnet/minecraft/client/gui/components/Whence;I)V", at = @At("RETURN"))
    private void extrasounds$moveCursor(CallbackInfo ci) {
        if (!this.soundHandler.isPosUpdated(this.cursor, this.selectCursor)) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
        this.soundHandler.setCursorStart(this.cursor);
        this.soundHandler.setCursorEnd(this.selectCursor);
    }
}
