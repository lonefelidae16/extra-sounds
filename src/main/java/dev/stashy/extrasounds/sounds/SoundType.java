package dev.stashy.extrasounds.sounds;

import dev.stashy.extrasounds.Mixers;
import net.minecraft.sound.SoundCategory;

public enum SoundType
{
    PICKUP(1f, Mixers.INVENTORY, "item.pickup"),
    PLACE(0.9f, Mixers.INVENTORY, "item.place"),
    HOTBAR(1f, Mixers.HOTBAR, "item.select"),
    EFFECT(1f, Mixers.EFFECTS, "effect"),
    CHAT(1f, Mixers.CHAT, "ui.chat"),
    CHAT_MENTION(1f, Mixers.CHAT_MENTION, "ui.chat"),
    TYPING(1f, Mixers.TYPING, "ui.typing"),
    ACTION(1f, Mixers.ACTION, "action");

    public final float pitch;
    public final SoundCategory category;
    public final String prefix;

    SoundType(float pitch, SoundCategory category, String prefix)
    {
        this.pitch = pitch;
        this.category = category;
        this.prefix = prefix;
    }
}
