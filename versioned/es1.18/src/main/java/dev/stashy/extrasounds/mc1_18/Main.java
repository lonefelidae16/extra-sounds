package dev.stashy.extrasounds.mc1_18;

import dev.stashy.extrasounds.logics.VersionedMain;
import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.Registry;

public final class Main extends VersionedMain {
    @Override
    public Identifier generateIdentifier(String namespace, String path) {
        return new Identifier(namespace, path);
    }

    @Override
    public Identifier getItemId(Item item) {
        return Registry.ITEM.getId(item);
    }

    @Override
    public VersionedSoundEventWrapper generateSoundEvent(Identifier id) {
        return VersionedSoundEventWrapper.newInstance(id);
    }

    @Override
    public IndexedIterable<Item> getItemRegistry() {
        return Registry.ITEM;
    }

    @Override
    public boolean canItemsCombine(ItemStack stack1, ItemStack stack2) {
        return ItemStack.canCombine(stack1, stack2);
    }

    @Override
    public void playSound(SoundInstance instance) {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.send(() -> client.getSoundManager().play(instance));
    }

    @Override
    public boolean shouldIgnoreItemSound(Item cursorItem, Item slotItem, InventoryClickState state) {
        var predicateCursor = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(cursorItem, null);
        var predicateSlot = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(slotItem, null);

        return (predicateCursor != null && predicateCursor.test(state)) || (predicateSlot != null && predicateSlot.test(state));
    }
}
