package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.Mixers;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public final class HotbarSoundHandler {
    public static final int FORCE_HOTBAR_CHANGE = -1;

    private Item pickingItem = Items.AIR;

    public void onSwap(Item mainHand, Item offHand) {
        if (offHand != Items.AIR) {
            ExtraSounds.MANAGER.playSound(offHand, SoundType.PICKUP);
        } else if (mainHand != Items.AIR) {
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

        final int selectedSlot = player.getInventory().selectedSlot;

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
        if (!player.getMainHandStack().isOf(item) && item != Items.AIR) {
            ExtraSounds.MANAGER.playSound(item, SoundType.HOTBAR);
        }
    }

    public void setPickingItem(Item item) {
        this.pickingItem = item;
    }

    public Item popPickingItem() {
        final Item result = this.pickingItem;
        this.pickingItem = Items.AIR;
        return result;
    }

    public void onThrow(ItemStack itemStack) {
        ExtraSounds.MANAGER.playThrow(itemStack, Mixers.HOTBAR);
    }
}
