package dev.stashy.extrasounds.mixin.inventory;

import dev.stashy.extrasounds.Mixers;
import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Creative screen scroll sound.
 */
@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public abstract class CreativeScreenHandlerMixin {
    @Unique
    private int lastPos = 0;
    @Unique
    private long lastTime = 0L;

    @Shadow
    protected abstract int getRow(float scroll);

    @Inject(method = "scrollItems", at = @At("HEAD"))
    private void extrasounds$creativeScreenScroll(float position, CallbackInfo ci) {
        final long now = System.currentTimeMillis();
        final long timeDiff = now - lastTime;
        final int row = this.getRow(position);
        if (timeDiff > 20 && lastPos != row && !(lastPos != 1 && row == 0)) {
            SoundManager.playSound(
                    Sounds.INVENTORY_SCROLL,
                    (1f - 0.1f + 0.1f * Math.min(1, 50f / timeDiff)),
                    Mixers.SCROLL);
            lastTime = now;
            lastPos = row;
        }
    }
}
