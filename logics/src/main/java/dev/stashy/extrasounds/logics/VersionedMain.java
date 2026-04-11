package dev.stashy.extrasounds.logics;

import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import dev.stashy.extrasounds.sounds.SoundType;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.IdMap;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

    public abstract Identifier getItemId(Item item);

    public abstract VersionedSoundEventWrapper generateSoundEvent(Identifier id);

    public abstract IdMap<Item> getItemRegistry();

    public abstract boolean canItemsCombine(ItemStack stack1, ItemStack stack2);

    public abstract void playSound(SoundInstance instance);

    public abstract boolean shouldIgnoreItemSound(Item cursorItem, Item slotItem, InventoryClickState state);

    public abstract float getSoundVolume(SoundSource soundCategory);

    public abstract void stopSound(VersionedSoundEventWrapper event, SoundType type);

    public final Identifier[] getItemIdWithComponents(ItemStack itemStack, DataComponentType<?>... components) {
        final List<Identifier> result = new ArrayList<>();
        result.add(this.getItemId(itemStack.getItem()));

        if (components == null) {
            return result.toArray(Identifier[]::new);
        }

        final DataComponentMap map = itemStack.getComponents();
        for (var component : components) {
            if (map.has(component) && (map.get(component) instanceof Identifier id) && !result.contains(id)) {
                result.addFirst(id);
            }
        }

        return result.toArray(Identifier[]::new);
    }
}
