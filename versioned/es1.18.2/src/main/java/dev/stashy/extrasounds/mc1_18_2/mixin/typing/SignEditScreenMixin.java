package dev.stashy.extrasounds.mc1_18_2.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignEditScreen.class)
public abstract class SignEditScreenMixin {
    @Unique
    private int previousRow;
    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();

    @Shadow
    private int currentRow;

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void extrasounds$moveRow(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.currentRow != this.previousRow) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
            this.previousRow = this.currentRow;
        }
    }
}
