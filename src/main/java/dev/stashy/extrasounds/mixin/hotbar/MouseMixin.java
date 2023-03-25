package dev.stashy.extrasounds.mixin.hotbar;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Hotbar scroll action.
 */
@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Shadow
    private @Final MinecraftClient client;

    @Inject(
            method = "onMouseScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$hotbarScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        final ClientPlayerEntity player = this.client.player;
        if (player == null) {
            return;
        }

        SoundManager.hotbar(player.getInventory().selectedSlot);
    }
}
