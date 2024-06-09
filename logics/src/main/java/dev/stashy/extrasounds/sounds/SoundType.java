package dev.stashy.extrasounds.sounds;

import dev.stashy.extrasounds.logics.Mixers;
import net.minecraft.sound.SoundCategory;

public enum SoundType {
    PICKUP(1f, Mixers.INVENTORY, "item.pickup"),
    PLACE(0.9f, Mixers.INVENTORY, "item.place"),
    HOTBAR(1f, Mixers.HOTBAR, "item.select"),
    CHAT(1f, Mixers.CHAT, "ui.chat"),
    CHAT_MENTION(1f, Mixers.CHAT_MENTION, "ui.chat"),
    EFFECTS(1f, Mixers.EFFECTS, "effects"),
    TYPING(1f, Mixers.TYPING, "ui.typing"),
    ITEM_INTR(1f, Mixers.ITEM_INTR, "item.interact"),
    BLOCK_INTR(1f, Mixers.BLOCK_INTR, "block.interact"),
    ENTITY(1f, Mixers.ENTITY, "entity");

    public final float pitch;
    public final SoundCategory category;
    public final String prefix;

    SoundType(float pitch, SoundCategory category, String prefix) {
        this.pitch = pitch;
        this.category = category;
        this.prefix = prefix;
    }
}
