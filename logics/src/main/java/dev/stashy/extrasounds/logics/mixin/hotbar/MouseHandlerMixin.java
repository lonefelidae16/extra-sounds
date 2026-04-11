package dev.stashy.extrasounds.logics.mixin.hotbar;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * For Hotbar scroll action.
 */
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @WrapOperation(
            method = "onScroll",
            at = {
                    @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"),
                    @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlotDeferred(I)V")
            },
            require = 1
    )
    private void extrasounds$hotbarScroll(Inventory instance, int slot, Operation<Void> original) {
        this.soundHandler.onChange(slot);
        original.call(instance, slot);
    }
}
