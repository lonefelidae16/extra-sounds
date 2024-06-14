package dev.stashy.extrasounds.logics.mixin.inventory;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.Mixers;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Screen open/close sound.
 */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "setScreen")
    private void extrasounds$screenChange(@Nullable Screen screen, CallbackInfo ci) {
        if (currentScreen != screen && screen instanceof HandledScreen && !(screen instanceof CreativeInventoryScreen)) {
            ExtraSounds.MANAGER.playSound(Sounds.INVENTORY_OPEN, 1f, Mixers.INVENTORY);
        } else if (screen == null && currentScreen instanceof HandledScreen) {
            ExtraSounds.MANAGER.playSound(Sounds.INVENTORY_CLOSE, 1f, Mixers.INVENTORY);
        }
    }
}
