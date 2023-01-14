package dev.stashy.extrasounds.mixin.inventory;

import dev.stashy.extrasounds.Mixers;
import dev.stashy.extrasounds.SoundManager;
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

@Mixin(MinecraftClient.class)
public abstract class InventoryScreenSounds
{

    @Shadow
    @Nullable
    public Screen currentScreen;

    @Inject(at = @At("HEAD"), method = "setScreen")
    void open(@Nullable Screen screen, CallbackInfo ci)
    {
        if (currentScreen != screen && screen instanceof HandledScreen && !(screen instanceof CreativeInventoryScreen))
            SoundManager.playSound(Sounds.INVENTORY_OPEN, 1f, Mixers.INVENTORY);
        else if (screen == null && currentScreen instanceof HandledScreen)
            SoundManager.playSound(Sounds.INVENTORY_CLOSE, 1f, Mixers.INVENTORY);
    }
}
