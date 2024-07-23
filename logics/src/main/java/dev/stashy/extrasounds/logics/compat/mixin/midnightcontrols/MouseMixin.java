package dev.stashy.extrasounds.logics.compat.mixin.midnightcontrols;

import dev.stashy.extrasounds.logics.ExtraSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public abstract class MouseMixin {
    @Unique
    private int currentHotbarSlot = -1;
    /**
     * The lambda of {@code MinecraftClient#execute(() -> { ... })}
     */
    @Unique
    private static final String METHOD_SIGN_SETUP_CALLBACK_LAMBDA = "method_22686";
    @Unique
    private static final String METHOD_SIGN_ON_MOUSE_BUTTON = "Lnet/minecraft/client/Mouse;onMouseButton(JIII)V";

    @Inject(method = METHOD_SIGN_SETUP_CALLBACK_LAMBDA, at = @At(value = "INVOKE", target = METHOD_SIGN_ON_MOUSE_BUTTON), require = 0)
    private void extrasounds$storeHotbarIndex_integrateMidnightControls(long windowx, int button, int action, int modifiers, CallbackInfo ci) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || button != 0) {
            return;
        }
        this.currentHotbarSlot = player.getInventory().selectedSlot;
    }

    @Inject(method = METHOD_SIGN_SETUP_CALLBACK_LAMBDA, at = @At(value = "INVOKE", target = METHOD_SIGN_ON_MOUSE_BUTTON, shift = At.Shift.AFTER), require = 0)
    private void extrasounds$touchHotbar_integrateMidnightControls(long windowx, int button, int action, int modifiers, CallbackInfo ci) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || button != 0) {
            return;
        }
        final int selectedSlot = player.getInventory().selectedSlot;
        if (selectedSlot != this.currentHotbarSlot) {
            ExtraSounds.MANAGER.hotbar(selectedSlot);
        }
    }
}
