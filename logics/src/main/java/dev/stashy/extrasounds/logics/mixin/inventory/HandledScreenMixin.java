package dev.stashy.extrasounds.logics.mixin.inventory;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Set;

/**
 * For {@link net.minecraft.screen.slot.SlotActionType#QUICK_CRAFT} sound on Inventory.
 */
@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow
    protected @Final Set<Slot> cursorDragSlots;

    @Inject(method = "mouseDragged", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void extrasounds$quickCraftSound(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir, Slot slot) {
        if (!cursorDragSlots.contains(slot) && !cursorDragSlots.isEmpty()) {
            ExtraSounds.MANAGER.playSound(Sounds.ITEM_DRAG, SoundType.PLACE);
        }
    }
}
