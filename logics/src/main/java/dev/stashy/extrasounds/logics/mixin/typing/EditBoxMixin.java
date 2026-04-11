package dev.stashy.extrasounds.logics.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();

    // #region method signatures
    // <editor-fold desc="method signatures">
    @Unique
    private static final String METHOD_SIGN_SET_CURSOR = "Lnet/minecraft/client/gui/components/EditBox;moveCursorTo(IZ)V";
    @Unique
    private static final String METHOD_SIGN_MOVE_CURSOR = "Lnet/minecraft/client/gui/components/EditBox;moveCursor(IZ)V";
    @Unique
    private static final String METHOD_SIGN_CURSOR_TO_START = "Lnet/minecraft/client/gui/components/EditBox;moveCursorToStart(Z)V";
    @Unique
    private static final String METHOD_SIGN_CURSOR_TO_END = "Lnet/minecraft/client/gui/components/EditBox;moveCursorToEnd(Z)V";
    // </editor-fold>
    // #endregion

    @Shadow
    private int cursorPos;
    @Shadow
    private int highlightPos;

    @Shadow
    public abstract String getHighlighted();

    @Shadow
    public abstract String getValue();

    @Inject(method = "deleteText", at = @At("HEAD"))
    private void extrasounds$eraseStrHead(int offset, boolean shiftDown, CallbackInfo ci) {
        this.soundHandler.onCharErase(offset, this.getValue().length(), this.cursorPos, this.highlightPos);
    }

    @Inject(method = "deleteText", at = @At("RETURN"))
    private void extrasounds$eraseStrReturn(CallbackInfo ci) {
        this.soundHandler.setCursor(this.highlightPos);
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyboardHandler;setClipboard(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$cutAction(KeyEvent keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (!keyInput.isCut() || this.getHighlighted().isEmpty()) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.CUT);
        this.soundHandler.setCursor(this.highlightPos);
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void extrasounds$appendChar(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !this.soundHandler.isPosUpdated(this.cursorPos, this.highlightPos)) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.INSERT);
        this.soundHandler.setCursor(this.highlightPos);
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyboardHandler;getClipboard()Ljava/lang/String;",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$pasteAction(KeyEvent keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (!keyInput.isPaste() || !this.soundHandler.isPosUpdated(this.cursorPos, this.highlightPos)) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.PASTE);
        this.soundHandler.setCursor(this.highlightPos);
    }

    @Inject(method = "keyPressed",
            at = {
                    @At(value = "INVOKE", target = METHOD_SIGN_SET_CURSOR, shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = METHOD_SIGN_MOVE_CURSOR, shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_START, shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_END, shift = At.Shift.AFTER)
            }
    )
    private void extrasounds$cursorMoveKeyTyped(CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onCursorChanged(this.cursorPos, this.highlightPos);
    }

    @Inject(method = {"onClick", "onDrag"}, at = @At("RETURN"))
    private void extrasounds$clickEvent(CallbackInfo ci) {
        this.soundHandler.onCursorChanged(this.cursorPos, this.highlightPos);
    }

    @Inject(method = "setValue", at = @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_END))
    private void extrasounds$autoComplete(CallbackInfo ci) {
        this.soundHandler.setCursor(this.getValue().length());
    }
}
