package dev.stashy.extrasounds.mixin.inventory;

import dev.stashy.extrasounds.Mixers;
import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeInventoryScreen.CreativeScreenHandler.class)
public abstract class CreativeListScroll
{
    @Unique
    private int lastPos = 0;
    @Unique
    private long lastTime = 0L;

    @Shadow
    public @Final DefaultedList<ItemStack> itemList;

    protected int getRow(float scroll) {
        int i = (this.itemList.size() + 9 - 1) / 9 - 5;
        return (int)((double)(scroll * (float)i) + 0.5);
    }

    @Inject(method = "scrollItems", at = @At("HEAD"))
    void scroll(float position, CallbackInfo ci)
    {
        final long now = System.currentTimeMillis();
        final long timeDiff = now - lastTime;
        final int row = this.getRow(position);
        if (timeDiff > 20 && lastPos != row && !(lastPos != 1 && row == 0))
        {
            SoundManager.playSound(
                    Sounds.INVENTORY_SCROLL,
                    (1f - 0.1f + 0.1f * Math.min(1, 50f / timeDiff)),
                    Mixers.SCROLL);
            lastTime = now;
            lastPos = row;
        }
    }
}
