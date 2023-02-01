package dev.stashy.extrasounds.sounds;

import dev.stashy.extrasounds.ExtraSounds;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

import java.util.List;

public class Sounds
{
    public static final SoundEvent CHAT = SoundEvent.of(new Identifier(ExtraSounds.MODID, "chat.message"));
    public static final SoundEvent CHAT_MENTION = SoundEvent.of(new Identifier(ExtraSounds.MODID, "chat.mention"));
    public static final SoundEvent HOTBAR_SCROLL = SoundEvent.of(new Identifier(ExtraSounds.MODID, "hotbar_scroll"));
    public static final SoundEvent INVENTORY_OPEN = SoundEvent.of(new Identifier(ExtraSounds.MODID, "inventory.open"));
    public static final SoundEvent INVENTORY_CLOSE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "inventory.close"));
    public static final SoundEvent INVENTORY_SCROLL = SoundEvent.of(new Identifier(ExtraSounds.MODID, "inventory.scroll"));
    public static final SoundEvent ITEM_DROP = SoundEvent.of(new Identifier(ExtraSounds.MODID, "item.drop"));
    public static final SoundEvent ITEM_PICK = SoundEvent.of(new Identifier(ExtraSounds.MODID, "item.pickup"));
    public static final SoundEvent ITEM_PICK_ALL = SoundEvent.of(new Identifier(ExtraSounds.MODID, "item.pickup_all"));
    public static final SoundEvent ITEM_CLONE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "item.clone"));
    public static final SoundEvent ITEM_DELETE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "item.delete"));
    public static final SoundEvent ITEM_DRAG = SoundEvent.of(new Identifier(ExtraSounds.MODID, "item.drag"));
    public static final SoundEvent EFFECT_ADD_POSITIVE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "effect.add.positive"));
    public static final SoundEvent EFFECT_ADD_NEGATIVE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "effect.add.negative"));
    public static final SoundEvent EFFECT_REMOVE_POSITIVE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "effect.remove.positive"));
    public static final SoundEvent EFFECT_REMOVE_NEGATIVE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "effect.remove.negative"));
    public static final SoundEvent KEYBOARD_TYPE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "keyboard.type"));
    public static final SoundEvent KEYBOARD_MOVE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "keyboard.move"));
    public static final SoundEvent KEYBOARD_ERASE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "keyboard.erase"));
    public static final SoundEvent KEYBOARD_CUT = SoundEvent.of(new Identifier(ExtraSounds.MODID, "keyboard.cut"));
    public static final SoundEvent KEYBOARD_PASTE = SoundEvent.of(new Identifier(ExtraSounds.MODID, "keyboard.paste"));

    public static class Actions
    {
        public static final SoundEvent BOW_PULL = SoundEvent.of(new Identifier(ExtraSounds.MODID, "action.bow"));
        public static final SoundEvent REPEATER_ADD = SoundEvent.of(new Identifier(ExtraSounds.MODID, "action.repeater.add"));
        public static final SoundEvent REPEATER_RESET = SoundEvent.of(new Identifier(ExtraSounds.MODID, "action.repeater.reset"));
    }

    public static SoundEntry aliased(SoundEvent e)
    {
        return aliased(e, 1f);
    }

    public static SoundEntry aliased(SoundEvent e, float volume)
    {
        return single(e.getId(), volume, 1f, Sound.RegistrationType.SOUND_EVENT);
    }

    public static SoundEntry event(Identifier id)
    {
        return event(id, 0.6f);
    }

    public static SoundEntry event(Identifier id, float volume)
    {
        return single(id, volume, 1.7f, Sound.RegistrationType.SOUND_EVENT);
    }

    public static SoundEntry single(Identifier id, float volume, float pitch, Sound.RegistrationType type)
    {
        return new SoundEntry(List.of(
                new Sound(id.toString(), ConstantFloatProvider.create(volume), ConstantFloatProvider.create(pitch), 1,
                          type, false, false, 16)
        ), false, null);
    }
}
