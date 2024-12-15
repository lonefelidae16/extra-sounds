package dev.stashy.extrasounds.mc1_21_2;

import dev.stashy.extrasounds.logics.VersionedMain;
import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;

public final class Main extends VersionedMain {
    public Main() {
        IGNORE_SOUND_PREDICATE_MAP.remove(Items.BUNDLE);
    }

    @Override
    public Identifier generateIdentifier(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    @Override
    public Identifier getItemId(Item item) {
        return Registries.ITEM.getId(item);
    }

    @Override
    public VersionedSoundEventWrapper generateSoundEvent(Identifier id) {
        return VersionedSoundEventWrapper.newInstance(id);
    }

    @Override
    public IndexedIterable<Item> getItemRegistry() {
        return Registries.ITEM;
    }

    @Override
    public boolean canItemsCombine(ItemStack stack1, ItemStack stack2) {
        return ItemStack.areItemsAndComponentsEqual(stack1, stack2);
    }

    @Override
    public void playSound(SoundInstance instance) {
        final MinecraftClient client = MinecraftClient.getInstance();
        client.executeSync(() -> client.getSoundManager().play(instance));
    }

    @Override
    public boolean shouldIgnoreItemSound(Item cursorItem, Item slotItem, InventoryClickState state) {
        if (cursorItem instanceof BundleItem) {
            if ((!state.isRMB && slotItem != Items.AIR) || (state.isRMB && slotItem == Items.AIR)) {
                return true;
            }
        }

        if (slotItem instanceof BundleItem) {
            if (state.slot instanceof CreativeInventoryScreen.LockableSlot) {
                return false;
            }
            if ((state.isRMB && cursorItem == Items.AIR) || (!state.isRMB && cursorItem != Items.AIR)) {
                return true;
            }
        }

        var predicateCursor = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(cursorItem, null);
        var predicateSlot = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(slotItem, null);

        return (predicateCursor != null && predicateCursor.test(state)) || (predicateSlot != null && predicateSlot.test(state));
    }
}
