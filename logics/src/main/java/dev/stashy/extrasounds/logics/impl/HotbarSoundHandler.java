package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class HotbarSoundHandler {
    public static final int FORCE_HOTBAR_CHANGE = -1;

    public void onSwapEvent(Item mainHand, Item offHand) {
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

    public void onItemPick(Item item) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        if (!player.getMainHandStack().isOf(item)) {
            ExtraSounds.MANAGER.playSound(item, SoundType.HOTBAR);
        }
    }
}
