package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.sounds.SoundType;
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
}
