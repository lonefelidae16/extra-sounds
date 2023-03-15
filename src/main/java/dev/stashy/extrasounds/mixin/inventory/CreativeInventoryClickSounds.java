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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryClickSounds
        extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler>
{
    @Shadow
    private static ItemGroup selectedTab;

    @Shadow
    @Nullable
    private Slot deleteItemSlot;

    public CreativeInventoryClickSounds(CreativeInventoryScreen.CreativeScreenHandler screenHandler, PlayerInventory playerInventory, Text text)
    {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "onMouseClick", at = @At("HEAD"))
    private void extrasounds$creativeInventoryClickEvent(@Nullable Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci)
    {
        if (this.client == null || this.client.player == null)
        {
            return;
        }

        final boolean bOnHotbar = slot != null && slot.inventory instanceof PlayerInventory;

        if (actionType == SlotActionType.THROW && slot != null && slotId >= 0) {
            // CreativeInventory can drop items while holding anything on cursor
            final ItemStack slotStack = slot.getStack().copy();
            if (button == 1 && selectedTab != ItemGroups.INVENTORY) {
                if (bOnHotbar) {
                    // Pressed Ctrl + Q on Hotbar to delete the stack only when not in Inventory tab
                    SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
                    return;
                }
                // If not, it pressed on the slot in CreativeInventory tab
                slotStack.setCount(slotStack.getMaxCount());
            } else if (button == 0) {
                // Pressed Q key only
                slotStack.setCount(1);
            }
            SoundManager.playThrow(slotStack);
            return;
        }

        if (actionType == SlotActionType.QUICK_MOVE &&
                selectedTab != ItemGroups.INVENTORY &&
                bOnHotbar &&
                slot.hasStack()
        ) {
            SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
            return;
        }

        final ItemStack cursorStack = this.handler.getCursorStack().copy();
        if (!cursorStack.isEmpty()) {
            if (this.deleteItemSlot != null && slot == this.deleteItemSlot) {
                // Clicked deleteItemSlot
                SoundManager.playSound(Sounds.ITEM_DELETE, SoundType.PICKUP);
                return;
            }

            if (slotId > 0 &&
                    actionType != SlotActionType.QUICK_CRAFT &&
                    actionType != SlotActionType.PICKUP_ALL &&
                    !bOnHotbar
            ) {
                // Clicked on the slot in CreativeInventory tab except Hotbar
                SoundManager.playSound(cursorStack, SoundType.PLACE);
                return;
            }
        }

        SoundManager.handleInventorySlot(this.client.player, slot, slotId, cursorStack, actionType, button);
    }

    @Inject(method = "mouseReleased", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;setSelectedTab(Lnet/minecraft/item/ItemGroup;)V"), locals = LocalCapture.CAPTURE_FAILSOFT)
    void tabChange(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir, double offsetX, double offsetY, Iterator iterator, ItemGroup itemGroup)
    {
        if (selectedTab != itemGroup) {
            SoundManager.playSound(itemGroup.getIcon(), SoundType.PICKUP);
        }
    }
}
