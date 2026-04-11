package dev.stashy.extrasounds.mapping;

import net.minecraft.client.resources.sounds.SoundEventRegistration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SoundDefinition {
    public final SoundEventRegistration pickup;
    @Nullable
    public final SoundEventRegistration place;
    @Nullable
    public final SoundEventRegistration hotbar;

    private SoundDefinition(SoundEventRegistration sound) {
        this(sound, null, null);
    }

    private SoundDefinition(@NotNull SoundEventRegistration pickup, @Nullable SoundEventRegistration place, @Nullable SoundEventRegistration hotbar) {
        this.pickup = pickup;
        this.place = place;
        this.hotbar = hotbar;
    }

    public static SoundDefinition of(@NotNull SoundEventRegistration pickup, SoundEventRegistration place, SoundEventRegistration hotbar) {
        return new SoundDefinition(pickup, place, hotbar);
    }

    public static SoundDefinition of(@NotNull SoundEventRegistration sound) {
        return new SoundDefinition(sound);
    }

    /**
     * Fills entry of this instance.
     * If entry is null, parameter {@code filler} will be used.
     *
     * @param filler A {@link SoundEventRegistration} to be used if {@code null} contains.
     * @return The copy of {@code this} and not null-ize.
     * @see #fill(SoundDefinition)
     */
    public SoundDefinition fill(@NotNull SoundEventRegistration filler) {
        return new SoundDefinition(
                this.pickup,
                (this.place == null) ? filler : this.place,
                (this.hotbar == null) ? filler : this.hotbar
        );
    }

    /**
     * @param filler A {@link SoundDefinition} to be used if {@code null} contains,
     *               must be NotNull for all entries.
     * @return The copy of {@code this} and not null-ize.
     * @see #fill(SoundEventRegistration)
     */
    public SoundDefinition fill(@NotNull SoundDefinition filler) throws NullPointerException {
        return new SoundDefinition(
                this.pickup,
                (this.place == null) ? Objects.requireNonNull(filler.place) : this.place,
                (this.hotbar == null) ? Objects.requireNonNull(filler.hotbar) : this.hotbar
        );
    }

    @Override
    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (!(that instanceof SoundDefinition soundDefinition)) {
            return false;
        }
        return Objects.equals(soundDefinition.pickup, this.pickup) &&
                Objects.equals(soundDefinition.place, this.place) &&
                Objects.equals(soundDefinition.hotbar, this.hotbar);
    }
}
