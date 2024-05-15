package dev.stashy.extrasounds.mixin.typing;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.impl.TextFieldState;
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
    private final TextFieldState state = new TextFieldState();

    // #region method signatures
    // <editor-fold desc="method signatures">
    @Unique
    private static final String METHOD_SIGN_SET_CURSOR = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursor(IZ)V";
    @Unique
    private static final String METHOD_SIGN_MOVE_CURSOR = "Lnet/minecraft/client/gui/widget/TextFieldWidget;moveCursor(IZ)V";
    @Unique
    private static final String METHOD_SIGN_CURSOR_TO_START = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursorToStart(Z)V";
    @Unique
    private static final String METHOD_SIGN_CURSOR_TO_END = "Lnet/minecraft/client/gui/widget/TextFieldWidget;setCursorToEnd(Z)V";
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
        this.state.onErase(offset, this.getText().length(), this.selectionStart, this.selectionEnd);
    }
    @Inject(method = "erase", at = @At("RETURN"))
    private void extrasounds$eraseStrReturn(int offset, CallbackInfo ci) {
        this.state.setCursor(this.selectionEnd);
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
        SoundManager.keyboard(SoundManager.KeyType.CUT);
        this.state.setCursor(this.selectionEnd);
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void extrasounds$appendChar(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !this.state.isPosUpdated(this.selectionStart, this.selectionEnd)) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.INSERT);
        this.state.setCursor(this.selectionEnd);
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
        if (!Screen.isPaste(keyCode) || !this.state.isPosUpdated(this.selectionStart, this.selectionEnd)) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.PASTE);
        this.state.setCursor(this.selectionEnd);
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
        this.state.onCursorChanged(this.selectionStart, this.selectionEnd);
    }

    @Inject(method = "onClick", at = @At(value = "INVOKE", target = METHOD_SIGN_SET_CURSOR, shift = At.Shift.AFTER))
    private void extrasounds$clickEvent(double mouseX, double mouseY, CallbackInfo ci) {
        this.state.onCursorChanged(this.selectionStart, this.selectionEnd);
    }

    @Inject(method = "setText", at = @At(value = "INVOKE", target = METHOD_SIGN_CURSOR_TO_END))
    private void extrasounds$autoComplete(String text, CallbackInfo ci) {
        this.state.setCursor(this.getText().length());
    }
}
