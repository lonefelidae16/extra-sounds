package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class HotbarSoundHandler {
    public void onSwapEvent(Item mainHand, Item offHand) {
        if (offHand != Items.AIR) {
            ExtraSounds.MANAGER.playSound(offHand, SoundType.PICKUP);
        } else if (mainHand != Items.AIR) {
            ExtraSounds.MANAGER.playSound(mainHand, SoundType.PICKUP);
        }
    }

    public void onChange() {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        ExtraSounds.MANAGER.hotbar(player.getInventory().selectedSlot);
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
