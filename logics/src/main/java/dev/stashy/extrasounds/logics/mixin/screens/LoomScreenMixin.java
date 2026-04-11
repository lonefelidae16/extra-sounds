package dev.stashy.extrasounds.logics.mixin.screens;

import dev.stashy.extrasounds.logics.impl.ScreenScrollHandler;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
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
    private static final String FIELD_ID_TOP_ROW = "Lnet/minecraft/client/gui/screens/inventory/LoomScreen;startRow:I";

    @Unique
    private final ScreenScrollHandler soundHandler = new ScreenScrollHandler();

    @Shadow
    private int startRow;

    @Inject(
            method = "containerChanged",
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
        this.soundHandler.onScroll(this.startRow);
    }
}
