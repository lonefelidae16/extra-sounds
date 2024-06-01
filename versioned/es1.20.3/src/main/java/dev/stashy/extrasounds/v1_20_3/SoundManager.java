package dev.stashy.extrasounds.v1_20_3;

import dev.stashy.extrasounds.logics.VersionedSoundManager;
import net.minecraft.item.ItemStack;

public final class SoundManager extends VersionedSoundManager {
    @Override
    protected boolean canItemsCombine(ItemStack stack1, ItemStack stack2) {
        return ItemStack.canCombine(stack1, stack2);
    }
}
