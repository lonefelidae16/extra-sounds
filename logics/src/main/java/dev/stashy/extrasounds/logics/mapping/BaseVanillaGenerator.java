package dev.stashy.extrasounds.logics.mapping;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

public abstract class BaseVanillaGenerator {
    protected static String getItemIdPath(Item item) {
        return Registries.ITEM.getId(item).getPath();
    }
}
