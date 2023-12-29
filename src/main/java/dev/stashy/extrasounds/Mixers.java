package dev.stashy.extrasounds;

import dev.stashy.soundcategories.CategoryLoader;
import net.minecraft.sound.SoundCategory;

public class Mixers implements CategoryLoader {
    @Register(master = true, defaultLevel = 0.5f)
    public static SoundCategory MASTER;
    @Register
    public static SoundCategory INVENTORY;
    @Register
    public static SoundCategory HOTBAR;
    @Register
    public static SoundCategory CHAT;
    @Register
    public static SoundCategory CHAT_MENTION;
    @Register
    public static SoundCategory TYPING;
    @Register
    public static SoundCategory EFFECTS;
    @Register
    public static SoundCategory BOW_PULL;
    @Register
    public static SoundCategory ENTITY_DEATH;
    @Register
    public static SoundCategory REPEATER;
    @Register(toggle = true)
    public static SoundCategory ITEM_DROP;
}
