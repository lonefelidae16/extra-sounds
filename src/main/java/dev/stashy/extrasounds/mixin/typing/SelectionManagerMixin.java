package dev.stashy.extrasounds.mixin.typing;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.impl.TextFieldContainer;
import net.minecraft.client.util.SelectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(SelectionManager.class)
public abstract class SelectionManagerMixin {
    @Unique
    private final TextFieldContainer container = new TextFieldContainer();
    @Unique
    private boolean bPasteAction = false;

    @Unique
    private static final String METHOD_SIGN_DELETE = "delete(ILnet/minecraft/client/util/SelectionManager$SelectionType;)V";

    @Shadow
    private int selectionStart;
    @Shadow
    private int selectionEnd;
    @Shadow
    private @Final Supplier<String> stringGetter;

    @Inject(method = METHOD_SIGN_DELETE, at = @At("HEAD"))
    private void extrasounds$beforeDelete(int offset, SelectionManager.SelectionType selectionType, CallbackInfo ci) {
        final String text = this.stringGetter.get();
        if (this.container.canErase(offset, text.length(), this.selectionStart, this.selectionEnd)) {
            SoundManager.keyboard(SoundManager.KeyType.ERASE);
        }
    }
    @Inject(method = METHOD_SIGN_DELETE, at = @At("RETURN"))
    private void extrasounds$afterDelete(int offset, SelectionManager.SelectionType selectionType, CallbackInfo ci) {
        this.container.setCursor(this.selectionEnd);
    }

    @Inject(method = "cut", at = @At("HEAD"))
    private void extrasounds$cutAction(CallbackInfo ci) {
        if (this.selectionStart == this.selectionEnd) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.CUT);
    }

    @Inject(method = "cut", at = @At("RETURN"))
    private void extrasounds$afterCut(CallbackInfo ci) {
        this.container.setCursor(this.selectionEnd);
    }

    @Inject(method = "insert(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"))
    private void extrasounds$appendChar(String string, String insertion, CallbackInfo ci) {
        if (!this.container.isPosUpdated(this.selectionStart, this.selectionEnd)) {
            return;
        }
        if (this.bPasteAction) {
            SoundManager.keyboard(SoundManager.KeyType.PASTE);
            this.bPasteAction = false;
        } else if (insertion.equals("\n")) {
            SoundManager.keyboard(SoundManager.KeyType.RETURN);
        } else {
            SoundManager.keyboard(SoundManager.KeyType.INSERT);
        }
        this.container.setCursor(this.selectionEnd);
    }

    @Inject(method = "paste", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;insert(Ljava/lang/String;Ljava/lang/String;)V"))
    private void extrasounds$pasteAction(CallbackInfo ci) {
        this.bPasteAction = true;
    }

    @Inject(method = "updateSelectionRange(Z)V", at = @At("RETURN"))
    private void extrasounds$moveCursor(boolean shiftDown, CallbackInfo ci) {
        if (!this.container.isPosUpdated(this.selectionStart, this.selectionEnd)) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.CURSOR);
        this.container.setCursorStart(this.selectionStart);
        this.container.setCursorEnd(this.selectionEnd);
    }
}
