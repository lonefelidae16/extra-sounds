package dev.stashy.extrasounds.mapping;

import net.minecraft.client.sound.SoundEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SoundDefinition {
    public final SoundEntry pickup;
    @Nullable
    public final SoundEntry place;
    @Nullable
    public final SoundEntry hotbar;

    private SoundDefinition(SoundEntry sound) {
        this(sound, null, null);
    }

    private SoundDefinition(@NotNull SoundEntry pickup, @Nullable SoundEntry place, @Nullable SoundEntry hotbar) {
        this.pickup = pickup;
        this.place = place;
        this.hotbar = hotbar;
    }

    public static SoundDefinition of(@NotNull SoundEntry pickup, SoundEntry place, SoundEntry hotbar) {
        return new SoundDefinition(pickup, place, hotbar);
    }

    public static SoundDefinition of(@NotNull SoundEntry sound) {
        return new SoundDefinition(sound);
    }

    /**
     * Fills entry of this instance.
     * If entry is null, parameter <code>filler</code> will be used.
     *
     * @param filler A {@link SoundEntry} to be used if <code>null</code> contains.
     * @return The copy of <code>this</code> and not null-ize.
     * @see #fill(SoundDefinition)
     */
    public SoundDefinition fill(@NotNull SoundEntry filler) {
        return new SoundDefinition(
                this.pickup,
                (this.place == null) ? filler : this.place,
                (this.hotbar == null) ? filler : this.hotbar
        );
    }

    /**
     * @param filler A {@link SoundDefinition} to be used if <code>null</code> contains,
     *               must be NotNull for all entries.
     * @return The copy of <code>this</code> and not null-ize.
     * @see #fill(SoundEntry)
     */
    public SoundDefinition fill(@NotNull SoundDefinition filler) throws NullPointerException {
        return new SoundDefinition(
                this.pickup,
                (this.place == null) ? Objects.requireNonNull(filler.place) : this.place,
                (this.hotbar == null) ? Objects.requireNonNull(filler.hotbar) : this.hotbar
        );
    }
}
