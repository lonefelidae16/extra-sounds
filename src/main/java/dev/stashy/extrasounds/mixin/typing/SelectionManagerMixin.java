package dev.stashy.extrasounds.mixin.typing;

import dev.stashy.extrasounds.SoundManager;
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
    /**
     * Requires to store the current position to prevent excessive sounds in method <code>extrasounds$moveCursor</code>.<br>
     * Injected into <code>updateSelectionRange(Z)V</code>.
     *
     * @see SelectionManager#updateSelectionRange
     */
    @Unique
    private int cursorStart = 0;
    @Unique
    private int cursorEnd = 0;
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

    /**
     * Check the current position was updated.
     *
     * @return <code>true</code> if the position has changed.
     */
    @Unique
    private boolean extrasounds$isPosUpdated() {
        return this.cursorStart != this.selectionStart || this.cursorEnd != this.selectionEnd;
    }

    @Inject(method = METHOD_SIGN_DELETE, at = @At("HEAD"))
    private void extrasounds$beforeDelete(int offset, SelectionManager.SelectionType selectionType, CallbackInfo ci) {
        final String text = this.stringGetter.get();
        final boolean bHeadBackspace = offset < 0 && this.selectionStart <= 0;
        final boolean bTailDelete = offset > 0 && this.selectionEnd >= text.length();
        if ((bHeadBackspace || bTailDelete) && this.selectionStart == this.selectionEnd) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.ERASE);
    }
    @Inject(method = METHOD_SIGN_DELETE, at = @At("RETURN"))
    private void extrasounds$afterDelete(int offset, SelectionManager.SelectionType selectionType, CallbackInfo ci) {
        this.cursorStart = this.cursorEnd = this.selectionEnd;
    }

    @Inject(method = "cut", at = @At("RETURN"))
    private void extrasounds$cutAction(CallbackInfo ci) {
        if (this.selectionStart == this.selectionEnd) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.CUT);
        this.cursorStart = this.cursorEnd = this.selectionEnd;
    }

    @Inject(method = "insert(Ljava/lang/String;Ljava/lang/String;)V", at = @At("RETURN"))
    private void extrasounds$appendChar(String string, String insertion, CallbackInfo ci) {
        if (!this.extrasounds$isPosUpdated()) {
            return;
        }
        if (insertion.equals("\n")) {
            SoundManager.keyboard(SoundManager.KeyType.RETURN);
        } else if (this.bPasteAction) {
            SoundManager.keyboard(SoundManager.KeyType.PASTE);
            this.bPasteAction = false;
        } else {
            SoundManager.keyboard(SoundManager.KeyType.INSERT);
        }
        this.cursorStart = this.cursorEnd = this.selectionEnd;
    }

    @Inject(method = "paste", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/SelectionManager;insert(Ljava/lang/String;Ljava/lang/String;)V"))
    private void extrasounds$pasteAction(CallbackInfo ci) {
        this.bPasteAction = true;
    }

    @Inject(method = "updateSelectionRange(Z)V", at = @At("RETURN"))
    private void extrasounds$moveCursor(boolean shiftDown, CallbackInfo ci) {
        if (!this.extrasounds$isPosUpdated()) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.CURSOR);
        this.cursorStart = this.selectionStart;
        this.cursorEnd = this.selectionEnd;
    }
}
