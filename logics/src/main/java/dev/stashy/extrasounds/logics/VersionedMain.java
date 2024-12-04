package dev.stashy.extrasounds.logics;

import com.google.common.collect.Maps;
import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import me.lonefelidae16.groominglib.Util;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;

import java.util.Map;
import java.util.function.Predicate;

public abstract class VersionedMain {
    public static VersionedMain newInstance() {
        try {
            Class<VersionedMain> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "Main");
            return clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'Main'", ex);
        }
        return null;
    }

    /**
     * Map of an item which should not play sounds.<br>
     * Predicate in this value will be passed an instance of an {@link InventoryClickState}.<br>
     * Item -&gt; Predicate&lt;InventoryClickStatus&gt;
     */
    protected static final Map<Item, Predicate<InventoryClickState>> IGNORE_SOUND_PREDICATE_MAP = Util.make(Maps.newHashMap(), map -> {
        map.put(Items.BUNDLE, status -> {
            return status.isRMB && !(status.slot instanceof CreativeInventoryScreen.LockableSlot);
        });
    });

    public abstract Identifier generateIdentifier(String namespace, String path);

    public abstract Identifier getItemId(Item item);

    public abstract VersionedSoundEventWrapper generateSoundEvent(Identifier id);

    public abstract IndexedIterable<Item> getItemRegistry();

    public abstract boolean canItemsCombine(ItemStack stack1, ItemStack stack2);

    public abstract void playSound(SoundInstance instance);

    public abstract boolean shouldIgnoreItemSound(Item cursorItem, Item slotItem, InventoryClickState state);
}
