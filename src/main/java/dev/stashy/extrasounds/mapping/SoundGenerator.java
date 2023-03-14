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
     * Shorthand of the <code>SoundGenerator#of(String, String, Function)</code> method.<br>
     * If your mod supports another item namespace or different from the modId, use {@link SoundGenerator#of(String, String, Function)} instead.
     */
    public static SoundGenerator of(@NotNull String modId, @NotNull Function<Item, SoundDefinition> itemSoundGenerator) {
        return new SoundGenerator(modId, modId, itemSoundGenerator);
    }

    /**
     * @param namespace          The item namespace your mod uses.
     * @param modId              The ID of your mod defined in <code>fabric.mod.json</code> that can be obtained by the {@link net.fabricmc.loader.api.FabricLoader#getModContainer} method.
     * @param itemSoundGenerator The instance of the {@link Function} that converts from {@link Item} to {@link SoundDefinition}.
     * @see VanillaGenerator#generator
     */
    public static SoundGenerator of(@NotNull String namespace, @NotNull String modId, @NotNull Function<Item, SoundDefinition> itemSoundGenerator) {
        return new SoundGenerator(namespace, modId, itemSoundGenerator);
    }
}
