package dev.stashy.extrasounds.logics.mapping;

import net.minecraft.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * The generator API that provides your item sounds.
 */
public final class SoundGenerator {
    public final String namespace;
    public final Function<Item, SoundDefinition> itemSoundGenerator;

    private SoundGenerator(String namespace, Function<Item, SoundDefinition> itemSoundGenerator) {
        this.namespace = namespace;
        this.itemSoundGenerator = itemSoundGenerator;
    }

    /**
     * Shorthand of the method {@link SoundGenerator#of(String, Function)}.
     */
    public static SoundGenerator of(@NotNull Function<Item, SoundDefinition> itemSoundGenerator) {
        return new SoundGenerator("", itemSoundGenerator);
    }

    /**
     * Tells the sounds of your items and/or blocks to ExtraSounds.<br>
     * When the modId you define in <code>fabric.mod.json</code> and the target item's namespace are the same,
     * <code>namespace</code> parameter can be omitted.
     *
     * @param namespace          The item namespace your mod uses (can be omitted).
     * @param itemSoundGenerator The instance of a {@link Function} that converts from {@link Item} to {@link SoundDefinition}.
     * @see SoundGenerator#of(Function)
     */
    public static SoundGenerator of(@NotNull String namespace, @NotNull Function<Item, SoundDefinition> itemSoundGenerator) {
        return new SoundGenerator(namespace, itemSoundGenerator);
    }

    /**
     * Deprecated<br>
     * Parameter <code>modId</code> is no longer required. Use {@link SoundGenerator#of(String, Function)} instead.
     *
     * @see SoundGenerator#of(String, Function)
     */
    @Deprecated
    public static SoundGenerator of(@NotNull String namespace, @NotNull String modId, @NotNull Function<Item, SoundDefinition> itemSoundGenerator) {
        return new SoundGenerator(namespace, itemSoundGenerator);
    }
}
