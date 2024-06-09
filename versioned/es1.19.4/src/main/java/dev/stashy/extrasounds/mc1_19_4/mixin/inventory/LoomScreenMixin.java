package dev.stashy.extrasounds.mc1_19_4.mixin.inventory;

import dev.stashy.extrasounds.logics.impl.ScreenScrollHandler;
import net.minecraft.client.gui.screen.ingame.LoomScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For Loom screen scroll sound.
 */
@Mixin(LoomScreen.class)
public abstract class LoomScreenMixin {
    @Unique
    private static final String FIELD_ID_TOP_ROW = "Lnet/minecraft/client/gui/screen/ingame/LoomScreen;visibleTopRow:I";

    @Unique
    private final ScreenScrollHandler soundHandler = new ScreenScrollHandler();

    @Shadow
    private int visibleTopRow;

    @Inject(
            method = "onInventoryChanged",
            at = @At(
                    value = "FIELD",
                    target = FIELD_ID_TOP_ROW,
                    opcode = Opcodes.PUTFIELD
            )
    )
    private void extrasounds$loomScreenReset(CallbackInfo ci) {
        this.soundHandler.resetScrollPos();
    }

    @Inject(
            method = {"mouseScrolled", "mouseDragged"},
            at = @At(
                    value = "FIELD",
                    target = FIELD_ID_TOP_ROW,
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$loomScreenScroll(CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onScroll(this.visibleTopRow);
    }
}
