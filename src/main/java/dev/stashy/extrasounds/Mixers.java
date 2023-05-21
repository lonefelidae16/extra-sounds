package dev.stashy.extrasounds;

import dev.stashy.soundcategories.CategoryLoader;
import net.minecraft.sound.SoundCategory;

public class Mixers implements CategoryLoader {
    @Register(master = true, defaultLevel = 0.5f)
    public static SoundCategory MASTER;
    @Register
    public static SoundCategory INVENTORY;
    @Register(tooltip = "tooltip.soundCategory.extrasounds_action")
    public static SoundCategory ACTION;
    @Register
    public static SoundCategory CHAT;
    @Register
    public static SoundCategory CHAT_MENTION;
    @Register
    public static SoundCategory EFFECTS;
    @Register
    public static SoundCategory HOTBAR;
    @Register(defaultLevel = 0f)
    public static SoundCategory TYPING;
    @Register(toggle = true)
    public static SoundCategory ITEM_DROP;
}
