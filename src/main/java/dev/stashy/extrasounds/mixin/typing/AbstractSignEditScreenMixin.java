package dev.stashy.extrasounds.mixin.typing;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignEditScreen.class)
public abstract class AbstractSignEditScreenMixin {
    @Unique
    private int previousRow;

    @Shadow
    private int currentRow;

    @Inject(method = "keyPressed", at = @At("RETURN"))
    private void extrasounds$moveRow(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (this.currentRow != this.previousRow) {
            SoundManager.keyboard(SoundManager.KeyType.CURSOR);
            this.previousRow = this.currentRow;
        }
    }
}
