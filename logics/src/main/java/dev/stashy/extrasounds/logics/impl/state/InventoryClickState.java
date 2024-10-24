package dev.stashy.extrasounds.logics.impl.state;

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
public final class InventoryClickState {
    /**
     * Indicates clicked slot. If null, screen border or off-screen area was clicked.
     * Or {@code QuickCrafting} is finished - mouse button has been released.
     */
    public final @Nullable Slot slot;
    /**
     * Indicates the index of slot. There are two ways it can be less than zero - {@code -1}; shows screen border,
     * or {@link ScreenHandler#EMPTY_SPACE_SLOT_INDEX}; shows off-screen area.
     */
    public final int slotIndex;
    /**
     * Stores the copy of the stack holding on the cursor.
     */
    public final ItemStack cursorStack;
    /**
     * Indicates the type of action to be taken on the slot.
     */
    public final SlotActionType actionType;
    /**
     * Indicates mouse buttons, or slot index when {@link #actionType} is {@link SlotActionType#SWAP}.
     * Includes {@code QuickCraftStage} while dragging.
     */
    public final int button;
    /**
     * If {@code true}, Right Mouse Button was clicked reliably.
     */
    public final boolean isRMB;
    /**
     * Stores the current {@link InventoryTabType}.
     */
    public final InventoryTabType tabType;

    public InventoryClickState(@Nullable Slot slot, int slotIndex, ItemStack cursor, SlotActionType actionType, int button, InventoryTabType inventoryTabType) {
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

    /**
     * Returns the copy of {@link ItemStack} in the slot.
     *
     * @return {@link ItemStack} in the slot. {@link ItemStack#EMPTY} if slot is null.
     * Both are passed through to the {@link ItemStack#copy()} method.
     */
    public ItemStack getSlotStack() {
        return (this.slot == null) ? ItemStack.EMPTY.copy() : this.slot.getStack().copy();
    }

    /**
     * @return {@code true} if off-screen area was clicked.
     */
    public boolean isEmptySpaceClicked() {
        return this.slotIndex == ScreenHandler.EMPTY_SPACE_SLOT_INDEX && this.actionType != SlotActionType.QUICK_CRAFT;
    }

    private boolean isRightClick() {
        return (this.actionType != SlotActionType.THROW && this.actionType != SlotActionType.SWAP) && this.button == 1 ||
                this.actionType == SlotActionType.QUICK_CRAFT && this.getQuickCraftButton() == 1;
    }

    /**
     * @return {@code true} if the cursor item cannot be inserted in this slot.
     */
    public boolean isSlotBlocked() {
        if (this.slot == null || this.cursorStack.isEmpty()) {
            return false;
        }

        return !this.slot.canInsert(this.cursorStack) && !ExtraSounds.canItemsCombine(this.slot.getStack(), this.cursorStack);
    }

    public boolean isOnCreativeTab() {
        return this.tabType == InventoryTabType.CREATIVE;
    }

    /**
     * Returns the copy of {@link ItemStack} on the cursor.
     * If {@link #actionType} is {@link SlotActionType#SWAP}, returns slot stack instead of {@link #cursorStack}.
     *
     * @param player An instance of a {@link PlayerEntity} that owns inventory where this event triggered.
     * @return The copy of {@link ItemStack}.
     */
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
