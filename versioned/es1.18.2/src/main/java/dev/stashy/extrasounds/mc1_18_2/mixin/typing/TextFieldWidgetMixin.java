package dev.stashy.extrasounds.mc1_18_2.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();

    // #region method signatures
    // <editor-fold desc="method signatures">
    @Unique
    private static final String METHOD_SIGN_SET_CURSOR = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursor(I)V";
    @Unique
    private static final String METHOD_SIGN_MOVE_CURSOR = "Lnet/minecraft/client/gui/widget/TextFieldWidget;moveCursor(I)V";
    @Unique
    private static final String METHOD_SIGN_CURSOR_TO_START = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursorToStart()V";
    @Unique
    private static final String METHOD_SIGN_CURSOR_TO_END = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursorToEnd()V";
    // </editor-fold>
    // #endregion

    @Shadow
    private int selectionStart;
    @Shadow
    private int selectionEnd;

    @Shadow
    public abstract String getSelectedText();

    @Shadow
    public abstract String getText();

    @Inject(method = "erase", at = @At("HEAD"))
    private void extrasounds$eraseStrHead(int offset, CallbackInfo ci) {
        this.soundHandler.onCharErase(offset, this.getText().length(), this.selectionStart, this.selectionEnd);
    }
    @Inject(method = "erase", at = @At("RETURN"))
    private void extrasounds$eraseStrReturn(int offset, CallbackInfo ci) {
        this.soundHandler.setCursor(this.selectionEnd);
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Keyboard;setClipboard(Ljava/lang/String;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$cutAction(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Screen.isCut(keyCode) || this.getSelectedText().isEmpty()) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.CUT);
        this.soundHandler.setCursor(this.selectionEnd);
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void extrasounds$appendChar(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !this.soundHandler.isPosUpdated(this.selectionStart, this.selectionEnd)) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.INSERT);
        this.soundHandler.setCursor(this.selectionEnd);
    }

    @Inject(
            method = "keyPressed",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Keyboard;getClipboard()Ljava/lang/String;",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$pasteAction(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Screen.isPaste(keyCode) || !this.soundHandler.isPosUpdated(this.selectionStart, this.selectionEnd)) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.PASTE);
        this.soundHandler.setCursor(this.selectionEnd);
    }

    @Inject(method = "keyPressed",
            at = {
                    @At(value = "INVOKE", target = METHOD_SIGN_SET_CURSOR, shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = METHOD_SIGN_MOVE_CURSOR, shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_START, shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_END, shift = At.Shift.AFTER)
            }
    )
    private void extrasounds$cursorMoveKeyTyped(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onCursorChanged(this.selectionStart, this.selectionEnd);
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = METHOD_SIGN_SET_CURSOR, shift = At.Shift.AFTER))
    private void extrasounds$clickEvent(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onCursorChanged(this.selectionStart, this.selectionEnd);
    }

    @Inject(method = "setText", at = @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_END))
    private void extrasounds$autoComplete(String text, CallbackInfo ci) {
        this.soundHandler.setCursor(this.getText().length());
    }
}
