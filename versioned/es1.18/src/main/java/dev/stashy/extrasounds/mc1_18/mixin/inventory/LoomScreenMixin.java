package dev.stashy.extrasounds.mc1_18.mixin.inventory;

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
    private static final String FIELD_ID_BUTTON_ID = "Lnet/minecraft/client/gui/screen/ingame/LoomScreen;firstPatternButtonId:I";

    @Unique
    private final ScreenScrollHandler soundHandler = new ScreenScrollHandler();

    @Shadow
    private int firstPatternButtonId;

    @Inject(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = FIELD_ID_BUTTON_ID,
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$loomScreenInit(CallbackInfo ci) {
        this.soundHandler.setScroll(this.firstPatternButtonId);
    }

    @Inject(
            method = {"mouseScrolled", "mouseDragged"},
            at = @At(
                    value = "FIELD",
                    target = FIELD_ID_BUTTON_ID,
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$loomScreenScroll(CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onScroll(this.firstPatternButtonId);
    }
}
