package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.impl.state.InventoryTabType;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCreativeInventoryHandler {

    protected abstract InventoryTabType getTabType();

    protected abstract boolean isCreativeInventorySlot(Slot slot);

    protected abstract Slot getDeleteItemSlot();

    public void onClick(PlayerEntity player, @Nullable Slot slot, int slotId, int button, SlotActionType actionType, ItemStack cursor) {
        final boolean bOnHotbar = slot != null && !this.isCreativeInventorySlot(slot);
        final boolean bMatchDeleteSlot = slot != null && slot == this.getDeleteItemSlot();
        final InventoryClickState state = new InventoryClickState(slot, slotId, cursor, actionType, button, this.getTabType());
        final boolean bOnCreativeTab = state.isOnCreativeTab();

        if (player == null) {
            return;
        }

        // <editor-fold desc="Exception Procedures on Creative Inventory Screen">

        if (actionType == SlotActionType.THROW) {
            // When CreativeInventory is opened, can drop items from any slots while holding an item on cursor.
            final ItemStack slotStack = state.getSlotStack();
            if (bOnCreativeTab && (slotStack.getCount() == 1 || button == 1) && state.cursorStack.isEmpty() && bOnHotbar) {
                // When stack is gone, will not be popped.
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
            } else {
                if (button == 0) {
                    slotStack.setCount(1);
                } else if (button == 1 && bOnCreativeTab) {
                    // With holding the Ctrl key; (slotActionType == THROW && button == 1)
                    slotStack.setCount(slotStack.getMaxCount());
                }
                ExtraSounds.MANAGER.playThrow(slotStack);
            }
            return;
        }

        if (actionType == SlotActionType.QUICK_MOVE) {
            // With holding the Shift key; (slotActionType == QUICK_MOVE)
            if (bOnCreativeTab && bOnHotbar && slot.hasStack()) {
                // Quick move from Hotbar to Creative slots; stack will be deleted.
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
                return;
            }
            if (bMatchDeleteSlot) {
                // Shift + Click on deleteItemSlot; clearing Inventory.
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_ALL, SoundType.PICKUP);
                return;
            }
        }

        if (!state.cursorStack.isEmpty()) {
            if (bMatchDeleteSlot) {
                // Clicked deleteItemSlot.
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
                return;
            }

            if (state.isEmptySpaceClicked() && !bOnCreativeTab) {
                // On Inventory tab, entire stack will be thrown regardless of mouse buttons.
                ExtraSounds.MANAGER.playThrow(state.cursorStack);
                return;
            }

            if (bOnCreativeTab && !bOnHotbar) {
                if (ExtraSounds.canItemsCombine(state.getSlotStack(), state.cursorStack) && !state.isRMB) {
                    // Left Mouse Clicked on the same slot in CreativeInventory tab except Hotbar.
                    ExtraSounds.MANAGER.playSound(state.cursorStack.getItem(), SoundType.PICKUP);
                    return;
                } else if (slotId >= 0) {
                    // Clicking on another slot will delete or decrement the cursor stack.
                    ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
                    return;
                }
            }
        }

        // </editor-fold>

        ExtraSounds.MANAGER.handleInventorySlot(player, state);
    }
}
