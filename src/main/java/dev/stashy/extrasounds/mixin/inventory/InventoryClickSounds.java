package dev.stashy.extrasounds.mixin.inventory;

import dev.stashy.extrasounds.SoundManager;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class InventoryClickSounds
{
    @Inject(method = "clickSlot", at = @At("HEAD"))
    void extrasounds$clickEvent(int syncId, int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci)
    {
        if (player == null) {
            return;
        }
        ScreenHandler screenHandler = player.currentScreenHandler;
        if (screenHandler == null) {
            return;
        }

        Slot slot = (slotIndex >= 0) ? screenHandler.slots.get(slotIndex) : null;
        SoundManager.handleInventorySlot(player, slot, slotIndex, screenHandler.getCursorStack(), actionType, button);
    }
}
