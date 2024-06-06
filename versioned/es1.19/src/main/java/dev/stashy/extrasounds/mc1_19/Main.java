package dev.stashy.extrasounds.mc1_19;

import dev.stashy.extrasounds.logics.VersionedMain;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.util.registry.Registry;

public final class Main extends VersionedMain {
    @Override
    public Identifier generateIdentifier(String namespace, String path) {
        return Identifier.of(namespace, path);
    }

    @Override
    public Identifier fromItemRegistry(Item item) {
        return Registry.ITEM.getId(item);
    }

    @Override
    public SoundEvent generateSoundEvent(Identifier id) {
        return new SoundEvent(id);
    }

    @Override
    public IndexedIterable<Item> getItemRegistry() {
        return Registry.ITEM;
    }
}
