package dev.stashy.extrasounds.logics;

import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;

import java.lang.reflect.Constructor;

public abstract class VersionedMain {
    public static VersionedMain newInstance() {
        try {
            Class<VersionedMain> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE,"Main");
            Constructor<VersionedMain> init = clazz.getConstructor();
            return init.newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'Main'", ex);
        }
        return null;
    }

    public abstract Identifier generateIdentifier(String namespace, String path);

    public abstract Identifier fromItemRegistry(Item item);

    public abstract SoundEvent generateSoundEvent(Identifier id);

    public abstract IndexedIterable<Item> getItemRegistry();
}
