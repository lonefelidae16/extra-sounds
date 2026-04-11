package dev.stashy.extrasounds.logics.mixin.screens;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.AbstractCreativeInventoryHandler;
import dev.stashy.extrasounds.logics.impl.ScreenScrollHandler;
import dev.stashy.extrasounds.logics.impl.state.InventoryTabType;
import dev.stashy.extrasounds.logics.impl.state.SlotActionType;
import dev.stashy.extrasounds.logics.mixin.access.ItemPickerMenuAccessor;
import dev.stashy.extrasounds.sounds.SoundType;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
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
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Unique
    private static final CreativeModeTab.Type TYPE_INVENTORY = CreativeModeTab.Type.INVENTORY;
    @Unique
    private static final String METHOD_SIGN_SCROLL_ITEMS = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$ItemPickerMenu;scrollTo(F)V";

    @Unique
    private final ScreenScrollHandler screenScrollHandler = new ScreenScrollHandler();
    @Unique
    private CreativeModeTab currentTab = selectedTab;
    @Unique
    private final AbstractCreativeInventoryHandler inventoryHandler = new AbstractCreativeInventoryHandler() {
        @Override
        protected InventoryTabType getTabType() {
            if (selectedTab.getType() == TYPE_INVENTORY) {
                return InventoryTabType.INVENTORY;
            } else {
                return InventoryTabType.CREATIVE;
            }
        }

        @Override
        protected boolean isCreativeInventorySlot(Slot slot) {
            return CreativeModeInventoryScreenMixin.this.isCreativeSlot(slot);
        }

        @Override
        protected Slot getDeleteItemSlot() {
            return CreativeModeInventoryScreenMixin.this.destroyItemSlot;
        }
    };

    @Shadow
    private static CreativeModeTab selectedTab;
    @Shadow
    @Nullable
    private Slot destroyItemSlot;
    @Shadow
    private float scrollOffs;

    @Shadow
    abstract boolean isCreativeSlot(@Nullable Slot slot);

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "slotClicked", at = @At("HEAD"))
    private void extrasounds$creativeInventoryClickEvent(@Nullable Slot slot, int slotId, int button, ContainerInput input, CallbackInfo ci) {
        if (this.minecraft == null || this.minecraft.player == null) {
            return;
        }

        SlotActionType actionType = switch (input) {
            case PICKUP -> SlotActionType.PICKUP;
            case QUICK_MOVE -> SlotActionType.QUICK_MOVE;
            case SWAP -> SlotActionType.SWAP;
            case THROW -> SlotActionType.THROW;
            case CLONE -> SlotActionType.CLONE;
            case PICKUP_ALL -> SlotActionType.PICKUP_ALL;
            case QUICK_CRAFT -> SlotActionType.QUICK_CRAFT;
        };

        this.inventoryHandler.onClick(this.minecraft.player, slot, slotId, button, actionType, this.menu.getCarried());
    }

    @Inject(method = "selectTab", at = @At("HEAD"))
    private void extrasounds$tabChange(CreativeModeTab group, CallbackInfo ci) {
        if (this.currentTab != group) {
            ExtraSounds.MANAGER.playSound2D(group.getIconItem(), SoundType.DEFAULT);
            this.screenScrollHandler.resetScrollPos();
            this.currentTab = group;
        }
    }

    @Inject(method = "mouseDragged", at = @At(value = "INVOKE", target = METHOD_SIGN_SCROLL_ITEMS))
    private void extrasounds$creativeScreenScrollDrag(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMenu() instanceof ItemPickerMenuAccessor accessor) {
            this.screenScrollHandler.onScroll(accessor.invokeGetRowIndexForScroll(this.scrollOffs));
        }
    }

    @Inject(method = "mouseScrolled", at = @At(value = "INVOKE", target = METHOD_SIGN_SCROLL_ITEMS))
    private void extrasounds$creativeScreenScroll(CallbackInfoReturnable<Boolean> cir) {
        if (this.getMenu() instanceof ItemPickerMenuAccessor accessor) {
            this.screenScrollHandler.onScroll(accessor.invokeGetRowIndexForScroll(this.scrollOffs));
        }
    }

    @Inject(method = "resize(II)V", at = @At(value = "INVOKE", target = METHOD_SIGN_SCROLL_ITEMS))
    private void extrasounds$creativeScreenScrollOnResize(CallbackInfo ci) {
        if (this.getMenu() instanceof ItemPickerMenuAccessor accessor) {
            this.screenScrollHandler.onScroll(accessor.invokeGetRowIndexForScroll(this.scrollOffs));
        }
    }

    @Inject(method = "refreshSearchResults", at = @At("HEAD"))
    private void extrasounds$resetCreativeScrollPos(CallbackInfo ci) {
        this.screenScrollHandler.resetScrollPos();
    }
}
