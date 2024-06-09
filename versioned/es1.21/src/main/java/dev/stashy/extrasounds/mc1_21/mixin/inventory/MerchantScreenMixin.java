package dev.stashy.extrasounds.mc1_21.mixin.inventory;

import dev.stashy.extrasounds.logics.impl.ScreenScrollHandler;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For Merchant screen scroll sound.
 */
@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin {
    @Unique
    private static final String FIELD_ID_START_OFFSET = "Lnet/minecraft/client/gui/screen/ingame/MerchantScreen;indexStartOffset:I";

    @Unique
    private final ScreenScrollHandler soundHandler = new ScreenScrollHandler();

    @Shadow
    int indexStartOffset;

    @Inject(method = "init", at = @At("HEAD"))
    private void extrasounds$merchantScreenInit(CallbackInfo ci) {
        this.soundHandler.resetScrollPos();
    }

    @Inject(
            method = {"mouseScrolled", "mouseDragged"},
            at = @At(
                    value = "FIELD",
                    target = FIELD_ID_START_OFFSET,
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$merchantScreenScroll(CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onScroll(this.indexStartOffset);
    }
}
