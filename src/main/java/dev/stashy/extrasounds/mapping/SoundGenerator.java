package dev.stashy.extrasounds.mapping;

import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * The generator API that provides your item sounds.
 */
public final class SoundGenerator {
    public final String namespace;
    public final String modId;
    public final Function<Item, SoundDefinition> itemSoundGenerator;

    private SoundGenerator(String namespace, String modId, Function<Item, SoundDefinition> itemSoundGenerator) {
        this.namespace = namespace;
        this.modId = modId;
        this.itemSoundGenerator = itemSoundGenerator;
    }

    /**
     * @param namespace          The namespace your mod uses.
     * @param modId              The ID to use in the ExtraSounds' cache file. Can be the same as {@link SoundGenerator#namespace}.
     * @param itemSoundGenerator The instance of the {@link Function} that converts from {@link Item} to {@link SoundDefinition}.
     * @see VanillaGenerator#generator
     */
    public static SoundGenerator of(@NotNull String namespace, @NotNull String modId, @NotNull Function<Item, SoundDefinition> itemSoundGenerator) {
        return new SoundGenerator(namespace, modId, itemSoundGenerator);
    }
}
