package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;

/**
 * Helper class that handles click events on Inventory screens.
 */
public final class InventoryClickStatus {
    public final @Nullable Slot slot;
    public final int slotIndex;
    public final ItemStack cursorStack;
    public final SlotActionType actionType;
    public final int button;
    public final boolean isRMB;
    public final InventoryTabType tabType;

    public InventoryClickStatus(@Nullable Slot slot, int slotIndex, ItemStack cursor, SlotActionType actionType, int button, InventoryTabType inventoryTabType) {
        this.slot = slot;
        this.slotIndex = slotIndex;
        this.cursorStack = cursor.copy();
        this.actionType = actionType;
        this.button = button;
        this.tabType = inventoryTabType;
        this.isRMB = this.isRightClick();
    }

    public int getQuickCraftButton() {
        return ScreenHandler.unpackQuickCraftButton(this.button);
    }

    public boolean isQuickCrafting() {
        return this.actionType == SlotActionType.QUICK_CRAFT && ScreenHandler.unpackQuickCraftStage(this.button) < 2;
    }

    public ItemStack getSlotStack() {
        return (this.slot == null) ? ItemStack.EMPTY.copy() : this.slot.getStack().copy();
    }

    public boolean isEmptySpaceClicked() {
        return this.slotIndex == ScreenHandler.EMPTY_SPACE_SLOT_INDEX && this.actionType != SlotActionType.QUICK_CRAFT;
    }

    private boolean isRightClick() {
        return (this.actionType != SlotActionType.THROW && this.actionType != SlotActionType.SWAP) && this.button == 1 ||
                this.actionType == SlotActionType.QUICK_CRAFT && this.getQuickCraftButton() == 1;
    }

    public boolean isSlotBlocked() {
        if (this.slot == null || this.cursorStack.isEmpty()) {
            return false;
        }

        return !this.slot.canInsert(this.cursorStack) && !ExtraSounds.canItemsCombine(this.slot.getStack(), this.cursorStack);
    }

    public boolean isOnCreativeTab() {
        return this.tabType == InventoryTabType.CREATIVE;
    }

    public ItemStack getCursorStack(PlayerEntity player) {
        final ItemStack result;
        if (this.actionType == SlotActionType.SWAP) {
            // Swap event.
            if (PlayerInventory.isValidHotbarIndex(this.button)) {
                // Pressed hotbar key.
                result = player.getInventory().getStack(this.button).copy();
            } else {
                // Pressed offhand key.
                result = player.getOffHandStack().copy();
            }
        } else {
            result = this.cursorStack;
        }
        return result;
    }

    @Override
    public String toString() {
        return "slot = %s, slotIndex = %d, cursorStack = %s, action = %s, button = %d".formatted(
                (this.slot == null) ? "null" : this.slot.getClass(),
                this.slotIndex,
                this.cursorStack.toString(),
                this.actionType,
                this.button
        );
    }
}
