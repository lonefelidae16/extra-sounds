package dev.stashy.extrasounds.sounds;

import dev.stashy.extrasounds.Mixers;
import net.minecraft.sound.SoundCategory;

public enum SoundType {
    PICKUP(1f, Mixers.INVENTORY, "item.pickup"),
    PLACE(0.9f, Mixers.INVENTORY, "item.place"),
    HOTBAR(1f, Mixers.HOTBAR, "item.select"),
    CHAT(1f, Mixers.CHAT, "ui.chat"),
    CHAT_MENTION(1f, Mixers.CHAT_MENTION, "ui.chat"),
    TYPING(1f, Mixers.TYPING, "ui.typing"),
    EFFECTS(1f, Mixers.EFFECTS, "effect"),
    BOW_PULL(1f, Mixers.BOW_PULL, "bow_pull"),
    ENTITY_DEATH(1f, Mixers.ENTITY_DEATH, "entity_death"),
    REPEATER(1f, Mixers.REPEATER, "repeater");

    public final float pitch;
    public final SoundCategory category;
    public final String prefix;

    SoundType(float pitch, SoundCategory category, String prefix) {
        this.pitch = pitch;
        this.category = category;
        this.prefix = prefix;
    }
}
