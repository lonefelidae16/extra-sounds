package dev.stashy.extrasounds.mc1_20_2.mixin.hotbar;

import dev.stashy.extrasounds.logics.impl.HotbarSoundHandler;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Hotbar scroll action.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Unique
    private final HotbarSoundHandler soundHandler = new HotbarSoundHandler();

    @Inject(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$hotbarScroll(CallbackInfo ci) {
        this.soundHandler.onChange();
    }
}
