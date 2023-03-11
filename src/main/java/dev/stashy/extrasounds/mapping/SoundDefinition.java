package dev.stashy.extrasounds.mapping;

import net.minecraft.client.sound.SoundEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SoundDefinition
{
    public final SoundEntry pickup;
    @Nullable
    public final SoundEntry place;
    @Nullable
    public final SoundEntry hotbar;

    private SoundDefinition(SoundEntry sound)
    {
        this(sound, null, null);
    }

    private SoundDefinition(@NotNull SoundEntry pickup, @Nullable SoundEntry place, @Nullable SoundEntry hotbar)
    {
        this.pickup = pickup;
        this.place = place;
        this.hotbar = hotbar;
    }

    public static SoundDefinition of(@NotNull SoundEntry pickup, SoundEntry place, SoundEntry hotbar)
    {
        return new SoundDefinition(pickup, place, hotbar);
    }

    public static SoundDefinition of(@NotNull SoundEntry sound)
    {
        return new SoundDefinition(sound);
    }
}
