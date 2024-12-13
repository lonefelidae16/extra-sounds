package dev.stashy.extrasounds.logics.compat.mixin.midnightcontrols;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.HotbarSoundHandler;
import eu.midnightdust.midnightcontrols.client.controller.InputHandlers;
import eu.midnightdust.midnightcontrols.client.controller.PressAction;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(InputHandlers.class)
public abstract class InputHandlersMixin {
    @Unique
    private static final HotbarSoundHandler SOUND_HANDLER = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @Inject(method = "lambda$handleHotbar$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;setSelectedSlot(I)V", shift = At.Shift.AFTER), require = 0)
    private static void extrasounds$hotbarScroll_integrateMidnightControls(CallbackInfoReturnable<PressAction> cir) {
        SOUND_HANDLER.onChange();
    }

    @Inject(method = "lambda$handleHotbar$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V", shift = At.Shift.AFTER), require = 0)
    private static void extrasounds$hotbarScroll_integrateMidnightControls197(CallbackInfoReturnable<PressAction> cir) {
        SOUND_HANDLER.onChange();
    }

    @Inject(method = "lambda$handleHotbar$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V", shift = At.Shift.AFTER), require = 0)
    private static void extrasounds$hotbarScroll_integrateMidnightControls174(CallbackInfoReturnable<PressAction> cir) {
        SOUND_HANDLER.onChange();
    }

    @Inject(method = "lambda$handleHotbar$0", at = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", shift = At.Shift.AFTER), require = 0)
    private static void extrasounds$hotbarScroll_integrateMidnightControls150(CallbackInfoReturnable<PressAction> cir) {
        SOUND_HANDLER.onChange();
    }
}
