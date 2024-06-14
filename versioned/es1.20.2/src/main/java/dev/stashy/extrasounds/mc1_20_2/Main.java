package dev.stashy.extrasounds.mc1_20_2;

import dev.stashy.extrasounds.logics.VersionedMain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;

public final class Main extends VersionedMain {
    @Override
    public Identifier generateIdentifier(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    @Override
    public Identifier fromItemRegistry(Item item) {
        return Registries.ITEM.getId(item);
    }

    @Override
    public SoundEvent generateSoundEvent(Identifier id) {
        return SoundEvent.of(id);
    }

    @Override
    public IndexedIterable<Item> getItemRegistry() {
        return Registries.ITEM;
    }

    @Override
    public boolean canItemsCombine(ItemStack stack1, ItemStack stack2) {
        return ItemStack.canCombine(stack1, stack2);
    }
}
