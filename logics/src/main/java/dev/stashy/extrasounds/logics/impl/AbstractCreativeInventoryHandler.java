package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.VersionedSoundManager;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCreativeInventoryHandler {
    public enum TabType {
        CREATIVE,
        INVENTORY
    }

    protected abstract boolean canItemsCombine(ItemStack stack1, ItemStack stack2);

    protected abstract TabType getTabType();

    protected abstract boolean isCreativeInventorySlot(Slot slot);

    protected abstract Slot getDeleteItemSlot();

    public void onClick(PlayerEntity player, @Nullable Slot slot, int slotId, int button, SlotActionType actionType, ItemStack cursor) {
        final boolean bOnHotbar = slot != null && !this.isCreativeInventorySlot(slot);
        final ItemStack cursorStack = cursor.copy();

        if (actionType == SlotActionType.THROW &&
                slot != null &&
                slotId >= 0
        ) {
            // CreativeInventory can drop items while holding anything on cursor
            final ItemStack slotStack = slot.getStack().copy();
            if (this.getTabType() == TabType.CREATIVE && (slotStack.getCount() == 1 || button == 1) && cursorStack.isEmpty() && bOnHotbar) {
                // Item will be deleted
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
            } else {
                if (button == 0) {
                    slotStack.setCount(1);
                } else if (button == 1 && this.getTabType() == TabType.CREATIVE) {
                    slotStack.setCount(slotStack.getMaxCount());
                }
                ExtraSounds.MANAGER.playThrow(slotStack);
            }
            return;
        }

        if (slot != null && !bOnHotbar && cursorStack.isEmpty() && slot.getStack().isOf(Items.BUNDLE)) {
            // Bundle in Creative slot can be picked up with Right Mouse Click.
            ExtraSounds.MANAGER.handleInventorySlot(player, slot, slotId, cursorStack, actionType, 0);
            return;
        }

        if (actionType == SlotActionType.QUICK_MOVE) {
            if (this.getTabType() == TabType.CREATIVE && bOnHotbar && slot.hasStack()) {
                // Quick move from Hotbar to Creative slots.
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
                return;
            }
            if (this.getTabType() == TabType.INVENTORY && slot == this.getDeleteItemSlot()) {
                // Shift + click on deleteItemSlot.
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_ALL, SoundType.PICKUP);
                return;
            }
        }

        if (!cursorStack.isEmpty()) {
            if (this.getDeleteItemSlot() != null && slot == this.getDeleteItemSlot()) {
                // Clicked deleteItemSlot
                ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
                return;
            }

            if (slotId >= 0 &&
                    actionType != SlotActionType.QUICK_CRAFT &&
                    actionType != SlotActionType.PICKUP_ALL &&
                    !bOnHotbar &&
                    slot != null
            ) {
                if (this.canItemsCombine(slot.getStack(), cursorStack) && !VersionedSoundManager.RIGHT_CLICK_PREDICATE.test(actionType, button)) {
                    // Left Mouse Clicked on the same slot in CreativeInventory tab except Hotbar
                    ExtraSounds.MANAGER.playSound(cursorStack.getItem(), SoundType.PICKUP);
                } else {
                    // On another slot will delete the cursor stack
                    ExtraSounds.MANAGER.playSound(Sounds.ITEM_DELETE_PARTIAL, SoundType.PICKUP);
                }
                return;
            }
        }

        ExtraSounds.MANAGER.handleInventorySlot(player, slot, slotId, cursorStack, actionType, button);
    }
}
