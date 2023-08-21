package dev.stashy.extrasounds.compat.mixin.rei;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.impl.TextFieldState;
import me.shedaniel.rei.api.client.gui.widgets.TextField;
import me.shedaniel.rei.impl.client.gui.widget.basewidgets.TextFieldWidget;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin implements TextField {
    @Unique
    private final TextFieldState state = new TextFieldState();

    @Shadow(remap = false)
    protected int cursorPos;

    @Inject(method = "erase", at = @At("HEAD"), remap = false)
    private void extrasounds$eraseStrHead(int offset, CallbackInfo ci) {
        this.state.onErase(offset, this.getText().length(), this.cursorPos, this.cursorPos);
    }
    @Inject(method = "erase", at = @At("RETURN"), remap = false)
    private void extrasounds$eraseStrReturn(int offset, CallbackInfo ci) {
        this.state.setCursor(this.cursorPos);
    }

    @Inject(method = "charTyped", at = @At("RETURN"))
    private void extrasounds$appendChar(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || !this.state.isPosUpdated(this.cursorPos, this.cursorPos)) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.INSERT);
        this.state.setCursor(this.cursorPos);
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
        this.state.setCursor(this.cursorPos);
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
        if (!Screen.isPaste(keyCode) || !this.state.isPosUpdated(this.cursorPos, this.cursorPos)) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.PASTE);
        this.state.setCursor(this.cursorPos);
    }

    @Inject(method = "keyPressed",
            at = {
                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursor(I)V", shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursorTo(I)V", shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;setCursorToStart()V", shift = At.Shift.AFTER),
                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;setCursorToEnd()V", shift = At.Shift.AFTER)
            }
    )
    private void extrasounds$cursorMoveKeyTyped(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        this.state.onCursorChanged(this.cursorPos, this.cursorPos);
    }

    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursorTo(I)V", shift = At.Shift.AFTER))
    private void extrasounds$clickEvent(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        this.state.onCursorChanged(this.cursorPos, this.cursorPos);
    }
}
