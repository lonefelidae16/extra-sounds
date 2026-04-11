package dev.stashy.extrasounds.mc26_2.compat.mixin.rei;

//@Pseudo
//@Mixin(TextFieldWidget.class)
//public abstract class TextFieldWidgetMixin implements TextField {
//    @Unique
//    private final TextFieldHandler soundHandler = new TextFieldHandler();
//
//    @Shadow(remap = false)
//    protected int cursorPos;
//
//    @Inject(method = "erase", at = @At("HEAD"), remap = false)
//    private void extrasounds$eraseStrHead(int offset, CallbackInfo ci) {
//        this.soundHandler.onCharErase(offset, this.getText().length(), this.cursorPos, this.cursorPos);
//    }
//
//    @Inject(method = "erase", at = @At("RETURN"), remap = false)
//    private void extrasounds$eraseStrReturn(CallbackInfo ci) {
//        this.soundHandler.setCursor(this.cursorPos);
//    }
//
//    @Inject(method = "charTyped", at = @At("RETURN"))
//    private void extrasounds$appendChar(CallbackInfoReturnable<Boolean> cir) {
//        if (!cir.getReturnValue() || !this.soundHandler.isPosUpdated(this.cursorPos, this.cursorPos)) {
//            return;
//        }
//        this.soundHandler.onKey(TextFieldHandler.KeyType.INSERT);
//        this.soundHandler.setCursor(this.cursorPos);
//    }
//
//    @Inject(
//            method = "keyPressed",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/KeyboardHandler;setClipboard(Ljava/lang/String;)V",
//                    shift = At.Shift.AFTER
//            ), require = 0
//    )
//    private void extrasounds$cutAction(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
//        try {
//            if (!input.isCut() || this.getSelectedText().isEmpty()) {
//                return;
//            }
//            this.soundHandler.onKey(TextFieldHandler.KeyType.CUT);
//            this.soundHandler.setCursor(this.cursorPos);
//        } catch (Exception ignored) {
//        }
//    }
//
//    @Inject(
//            method = "keyPressed",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/KeyboardHandler;getClipboard()Ljava/lang/String;",
//                    shift = At.Shift.AFTER
//            ), require = 0
//    )
//    private void extrasounds$pasteAction(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
//        try {
//            if (!input.isPaste() || !this.soundHandler.isPosUpdated(this.cursorPos, this.cursorPos)) {
//                return;
//            }
//            this.soundHandler.onKey(TextFieldHandler.KeyType.PASTE);
//            this.soundHandler.setCursor(this.cursorPos);
//        } catch (Exception ignored) {
//        }
//    }
//
//    @Inject(method = "keyPressed",
//            at = {
//                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursor(I)V", shift = At.Shift.AFTER),
//                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursorTo(I)V", shift = At.Shift.AFTER),
//                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursorToStart()V", shift = At.Shift.AFTER),
//                    @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursorToEnd()V", shift = At.Shift.AFTER)
//            }
//    )
//    private void extrasounds$cursorMoveKeyTyped(CallbackInfoReturnable<Boolean> cir) {
//        this.soundHandler.onCursorChanged(this.cursorPos, this.cursorPos);
//    }
//
//    @Inject(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lme/shedaniel/rei/impl/client/gui/widget/basewidgets/TextFieldWidget;moveCursorTo(I)V", shift = At.Shift.AFTER))
//    private void extrasounds$clickEvent(CallbackInfoReturnable<Boolean> cir) {
//        this.soundHandler.onCursorChanged(this.cursorPos, this.cursorPos);
//    }
//}
