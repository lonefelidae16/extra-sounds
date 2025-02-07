package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.Mixers;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public abstract class VersionedHotbarSoundHandler {
    public static final int FORCE_HOTBAR_CHANGE = -1;

    private static final Item ITEM_EMPTY = Items.AIR;

    private Item pickingItem = ITEM_EMPTY;

    public abstract int getPlayerInventorySlot(PlayerEntity player);

    public static VersionedHotbarSoundHandler newInstance() {
        try {
            Class<VersionedHotbarSoundHandler> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "impl.HotbarSoundHandler");
            return clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Failed to initialize 'HotbarSoundHandler'", ex);
        }
        return null;
    }

    public void onSwap(Item mainHand, Item offHand) {
        if (offHand != ITEM_EMPTY) {
            ExtraSounds.MANAGER.playSound(offHand, SoundType.PICKUP);
        } else if (mainHand != ITEM_EMPTY) {
            ExtraSounds.MANAGER.playSound(mainHand, SoundType.PICKUP);
        }
    }

    public void onChange() {
        this.onChange(FORCE_HOTBAR_CHANGE);
    }

    public void onChange(int newSlot) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        final int selectedSlot = this.getPlayerInventorySlot(player);

        if (newSlot == FORCE_HOTBAR_CHANGE) {
            ExtraSounds.MANAGER.hotbar(selectedSlot);
        } else if (newSlot != selectedSlot) {
            ExtraSounds.MANAGER.hotbar(newSlot);
        }
    }

    public void spectatorHotbar() {
        ExtraSounds.MANAGER.playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
    }

    public void doItemPick(Item item) {
        this.storePickingItem(item);
        this.onItemPick();
    }

    public void storePickingItem(Item item) {
        this.setPickingItem(item);
    }

    public void onItemPick() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        final Item item = this.popPickingItem();
        if (!player.getMainHandStack().isOf(item) && item != ITEM_EMPTY) {
            ExtraSounds.MANAGER.playSound(item, SoundType.HOTBAR);
        }
    }

    public void setPickingItem(Item item) {
        this.pickingItem = item;
    }

    public Item popPickingItem() {
        final Item result = this.pickingItem;
        this.pickingItem = ITEM_EMPTY;
        return result;
    }

    public void onThrow(ItemStack itemStack) {
        ExtraSounds.MANAGER.playThrow(itemStack, Mixers.HOTBAR);
    }
}
