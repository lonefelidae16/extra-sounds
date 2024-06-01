package dev.stashy.extrasounds.logics.sounds;

import dev.stashy.extrasounds.logics.ExtraSounds;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

import java.util.List;

public final class Sounds {
    private Sounds() {
    }

    public static final SoundEvent MUTED = ExtraSounds.createEvent("muted");
    public static final SoundEvent CHAT = ExtraSounds.createEvent("chat.message");
    public static final SoundEvent CHAT_MENTION = ExtraSounds.createEvent("chat.mention");
    public static final SoundEvent HOTBAR_SCROLL = ExtraSounds.createEvent("hotbar_scroll");
    public static final SoundEvent INVENTORY_OPEN = ExtraSounds.createEvent("inventory.open");
    public static final SoundEvent INVENTORY_CLOSE = ExtraSounds.createEvent("inventory.close");
    public static final SoundEvent INVENTORY_SCROLL = ExtraSounds.createEvent("inventory.scroll");
    public static final SoundEvent ITEM_DROP = ExtraSounds.createEvent("item.drop");
    public static final SoundEvent ITEM_PICK = ExtraSounds.createEvent("item.pickup");
    public static final SoundEvent ITEM_PICK_ALL = ExtraSounds.createEvent("item.pickup_all");
    public static final SoundEvent ITEM_CLONE = ExtraSounds.createEvent("item.clone");
    public static final SoundEvent ITEM_DELETE_ALL = ExtraSounds.createEvent("item.delete_all");
    public static final SoundEvent ITEM_DELETE_PARTIAL = ExtraSounds.createEvent("item.delete_partial");
    public static final SoundEvent ITEM_DRAG = ExtraSounds.createEvent("item.drag");
    public static final SoundEvent EFFECT_ADD_POSITIVE = ExtraSounds.createEvent("effect.add.positive");
    public static final SoundEvent EFFECT_ADD_NEGATIVE = ExtraSounds.createEvent("effect.add.negative");
    public static final SoundEvent EFFECT_REMOVE_POSITIVE = ExtraSounds.createEvent("effect.remove.positive");
    public static final SoundEvent EFFECT_REMOVE_NEGATIVE = ExtraSounds.createEvent("effect.remove.negative");
    public static final SoundEvent KEYBOARD_TYPE = ExtraSounds.createEvent("keyboard.type");
    public static final SoundEvent KEYBOARD_MOVE = ExtraSounds.createEvent("keyboard.move");
    public static final SoundEvent KEYBOARD_ERASE = ExtraSounds.createEvent("keyboard.erase");
    public static final SoundEvent KEYBOARD_CUT = ExtraSounds.createEvent("keyboard.cut");
    public static final SoundEvent KEYBOARD_PASTE = ExtraSounds.createEvent("keyboard.paste");

    public static final class Actions {
        private Actions() {
        }

        public static final SoundEvent BOW_PULL = ExtraSounds.createEvent("action.bow");
        public static final SoundEvent REPEATER_ADD = ExtraSounds.createEvent("action.repeater.add");
        public static final SoundEvent REPEATER_RESET = ExtraSounds.createEvent("action.repeater.reset");
        public static final SoundEvent REDSTONE_COMPONENT_ON = ExtraSounds.createEvent("action.redstone_component.on");
        public static final SoundEvent REDSTONE_COMPONENT_OFF = ExtraSounds.createEvent("action.redstone_component.off");
        public static final SoundEvent REDSTONE_WIRE_CHANGE = ExtraSounds.createEvent("action.redstone_wire.change");
    }

    public static final class Entities {
        private Entities() {
        }

        public static final SoundEvent POOF = ExtraSounds.createEvent("entity.poof");
    }

    public static SoundEntry aliased(SoundEvent e) {
        return aliased(e, 1f);
    }

    public static SoundEntry aliased(SoundEvent e, float volume) {
        return single(e.getId(), volume, 1f, Sound.RegistrationType.SOUND_EVENT);
    }

    public static SoundEntry event(Identifier id) {
        return event(id, 0.6f);
    }

    public static SoundEntry event(Identifier id, float volume) {
        return single(id, volume, 1.7f, Sound.RegistrationType.SOUND_EVENT);
    }

    public static SoundEntry single(Identifier id, float volume, float pitch, Sound.RegistrationType type) {
        return new SoundEntry(List.of(
                new Sound(id.toString(), ConstantFloatProvider.create(volume), ConstantFloatProvider.create(pitch), 1,
                        type, false, false, 16)
        ), false, null);
    }
}
