package dev.stashy.extrasounds.logics;

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
    public static SoundCategory EFFECTS;
    @Register
    public static SoundCategory TYPING;
    @Register
    public static SoundCategory ITEM_INTR;
    @Register
    public static SoundCategory BLOCK_INTR;
    @Register(defaultLevel = 0.f)
    public static SoundCategory ENTITY;
    @Register(toggle = true)
    public static SoundCategory ITEM_DROP;
}
