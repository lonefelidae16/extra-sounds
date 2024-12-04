package dev.stashy.extrasounds.mc1_21_4.mixin.inventory;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.AbstractCreativeInventoryHandler;
import dev.stashy.extrasounds.logics.impl.ScreenScrollHandler;
import dev.stashy.extrasounds.logics.impl.state.InventoryTabType;
import dev.stashy.extrasounds.sounds.SoundType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
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
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {
    @Unique
    private static final ItemGroup.Type TYPE_INVENTORY = ItemGroup.Type.INVENTORY;
    @Unique
    private static final String METHOD_SIGN_SCROLL_ITEMS = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen$CreativeScreenHandler;scrollItems(F)V";

    @Unique
    private final ScreenScrollHandler soundHandler = new ScreenScrollHandler();
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
            return CreativeInventoryScreenMixin.this.isCreativeInventorySlot(slot);
        }

        @Override
        protected Slot getDeleteItemSlot() {
            return deleteItemSlot;
        }
    };

    @Shadow
    private static ItemGroup selectedTab;
    @Shadow
    @Nullable
    private Slot deleteItemSlot;
    @Shadow
    private float scrollPosition;

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

        this.inventoryHandler.onClick(this.client.player, slot, slotId, button, actionType, this.handler.getCursorStack());
    }

    @Inject(method = "setSelectedTab", at = @At("HEAD"))
    private void extrasounds$tabChange(ItemGroup group, CallbackInfo ci) {
        if (selectedTab != group) {
            ExtraSounds.MANAGER.playSound(group.getIcon().getItem(), SoundType.PICKUP);
            this.soundHandler.resetScrollPos();
        }
    }

    @Inject(method = "mouseDragged", at = @At(value = "INVOKE", target = METHOD_SIGN_SCROLL_ITEMS))
    private void extrasounds$creativeScreenScroll(double mouseX, double mouseY, int button, double deltaX, double deltaY, CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onScroll(this.getScreenHandler().getRow(this.scrollPosition));
    }

    @Inject(method = "mouseScrolled", at = @At(value = "INVOKE", target = METHOD_SIGN_SCROLL_ITEMS))
    private void extrasounds$creativeScreenScroll(double mouseX, double mouseY, double horizontalAmount, double verticalAmount, CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onScroll(this.getScreenHandler().getRow(this.scrollPosition));
    }

    @Inject(method = "resize", at = @At(value = "INVOKE", target = METHOD_SIGN_SCROLL_ITEMS))
    private void extrasounds$creativeScreenScroll(MinecraftClient client, int width, int height, CallbackInfo ci) {
        this.soundHandler.onScroll(this.getScreenHandler().getRow(this.scrollPosition));
    }

    @Inject(method = "search", at = @At("HEAD"))
    private void extrasounds$resetCreativeScrollPos(CallbackInfo ci) {
        this.soundHandler.resetScrollPos();
    }
}
