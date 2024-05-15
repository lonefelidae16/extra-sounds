package dev.stashy.extrasounds.mixin.inventory;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For Creative screen sound.
 */
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Unique
    private static final ItemGroup.Type TYPE_INVENTORY = ItemGroup.Type.INVENTORY;

    @Shadow
    private static ItemGroup selectedTab;
    @Shadow
    @Nullable
    private Slot deleteItemSlot;

    @Shadow
    protected abstract boolean isClickInTab(ItemGroup group, double mouseX, double mouseY);
    @Shadow
    abstract boolean isCreativeInventorySlot(@Nullable Slot slot);

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "onMouseClick", at = @At("HEAD"))
    private void extrasounds$creativeInventoryClickEvent(@Nullable Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (this.client == null || this.client.player == null) {
            return;
        }

        final boolean bOnHotbar = slot != null && !this.isCreativeInventorySlot(slot);
        final ItemStack cursorStack = this.handler.getCursorStack().copy();

        if (actionType == SlotActionType.THROW &&
                slot != null &&
                slotId >= 0
        ) {
            // CreativeInventory can drop items while holding anything on cursor
            final ItemStack slotStack = slot.getStack().copy();
            if (selectedTab.getType() != TYPE_INVENTORY && (slotStack.getCount() == 1 || button == 1) && cursorStack.isEmpty() && bOnHotbar) {
                // Item will be deleted
                SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
            } else {
                if (button == 0) {
                    slotStack.setCount(1);
                } else if (button == 1 && selectedTab.getType() != TYPE_INVENTORY) {
                    slotStack.setCount(slotStack.getMaxCount());
                }
                SoundManager.playThrow(slotStack);
            }
            return;
        }

        if (actionType == SlotActionType.QUICK_MOVE &&
                selectedTab.getType() != TYPE_INVENTORY &&
                bOnHotbar &&
                slot.hasStack()
        ) {
            SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
            return;
        }

        if (!cursorStack.isEmpty()) {
            if (this.deleteItemSlot != null && slot == this.deleteItemSlot) {
                // Clicked deleteItemSlot
                SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
                return;
            }

            if (slotId >= 0 &&
                    actionType != SlotActionType.QUICK_CRAFT &&
                    actionType != SlotActionType.PICKUP_ALL &&
                    !bOnHotbar &&
                    slot != null
            ) {
                if (ItemStack.canCombine(slot.getStack(), cursorStack) && !SoundManager.RIGHT_CLICK_PREDICATE.test(actionType, button)) {
                    // Left Mouse Clicked on the same slot in CreativeInventory tab except Hotbar
                    SoundManager.playSound(cursorStack, SoundType.PICKUP);
                } else {
                    // On another slot will delete the cursor stack
                    SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
                }
                return;
            }
        }

        SoundManager.handleInventorySlot(this.client.player, slot, slotId, cursorStack, actionType, button);
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void extrasounds$tabChange(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        if (button != 0) {
            return;
        }

        final double screenX = mouseX - this.x;
        final double screenY = mouseY - this.y;
        for (ItemGroup itemGroup : ItemGroups.getGroupsToDisplay()) {
            if (this.isClickInTab(itemGroup, screenX, screenY) && selectedTab != itemGroup) {
                SoundManager.playSound(itemGroup.getIcon(), SoundType.PICKUP);
                return;
            }
        }
    }
}
