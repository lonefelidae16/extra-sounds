package dev.stashy.extrasounds;

import com.google.common.collect.Maps;
import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import dev.stashy.extrasounds.throwable.NoSuchSoundException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiPredicate;

public class SoundManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Random MC_RANDOM = Random.create();

    /**
     * Predicate of Right Mouse Click.
     */
    public static final BiPredicate<SlotActionType, Integer> RIGHT_CLICK_PREDICATE = (actionType, button) -> {
        return (actionType != SlotActionType.THROW && actionType != SlotActionType.SWAP) && button == 1 ||
                actionType == SlotActionType.QUICK_CRAFT && ScreenHandler.unpackQuickCraftButton(button) == 1;
    };

    /**
     * Map of the item which should not play sounds.<br>
     * BiPredicate in this value will be passed <code>SlotActionType</code> and <code>int</code> of button ID.<br>
     * Item -&gt; BiPredicate&lt;SlotActionType, Integer&gt;
     */
    private static final Map<Item, BiPredicate<SlotActionType, Integer>> IGNORE_SOUND_PREDICATE_MAP = Util.make(Maps.newHashMap(), map -> {
        map.put(Items.BUNDLE, RIGHT_CLICK_PREDICATE);
    });

    private static long lastPlayed = 0;
    private static Item quickMovingItem = Items.AIR;

    public enum KeyType {
        ERASE,
        CUT,
        INSERT,
        PASTE,
        RETURN,
        CURSOR
    }

    public enum EffectType {
        ADD,
        REMOVE
    }

    public static void hotbar(int i) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getInventory().getStack(i);
        if (stack.getItem() == Items.AIR) {
            playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
        } else {
            playSound(stack, SoundType.HOTBAR);
        }
    }

    public static void inventoryClick(ItemStack inSlot, ItemStack onCursor, SlotActionType actionType) {
        final boolean hasCursor = !onCursor.isEmpty();
        final boolean hasSlot = !inSlot.isEmpty();
        if (!hasCursor && !hasSlot) {
            return;
        }

        switch (actionType) {
            case PICKUP_ALL -> {
                if (hasCursor) {
                    playSound(Sounds.ITEM_PICK_ALL, SoundType.PICKUP);
                }
            }
            case THROW -> {
                if (!hasCursor) {
                    playThrow(inSlot);
                }
            }
            case QUICK_MOVE -> {
                if (hasSlot) {
                    handleQuickMoveSound(inSlot);
                }
            }
            default -> {
                /*
                 * hasCursor == true, hasSlot == true
                 *  --> ItemStack#canCombine ? PLACE : EXCHANGE;
                 *
                 * hasCursor == true, hasSlot == false
                 *  --> PLACE
                 *
                 * hasCursor == false, hasSlot == true
                 *  --> PICKUP
                 */
                if (hasSlot && hasCursor) {
                    if (ItemStack.canCombine(inSlot, onCursor)) {
                        playSound(onCursor, SoundType.PLACE);
                    } else {
                        playSound(inSlot, SoundType.PICKUP);
                    }
                } else if (hasCursor) {
                    playSound(onCursor, SoundType.PLACE);
                } else {
                    playSound(inSlot, SoundType.PICKUP);
                }
            }
        }
    }

    /**
     * Handles Click and KeyPress on inventory
     *
     * @param player     player instance
     * @param slot       slot in ScreenHandler
     * @param slotIndex  slotIndex
     * @param cursor     item that held by cursor
     * @param actionType action type
     * @param button     clicked mouse, pressed key or including QuickCraftStage
     */
    public static void handleInventorySlot(PlayerEntity player, @Nullable Slot slot, int slotIndex, ItemStack cursor, SlotActionType actionType, int button) {
        if (actionType == SlotActionType.QUICK_CRAFT && ScreenHandler.unpackQuickCraftStage(button) < 2) {
            // while dragging
            return;
        }
        if (slotIndex == -1) {
            // screen border clicked
            return;
        }

        // Determine Slot item
        final ItemStack slotItem = (slot == null) ? ItemStack.EMPTY : slot.getStack().copy();
        if (actionType == SlotActionType.QUICK_MOVE) {
            // cursor holding an item, then Shift + mouse (double) click
            handleQuickMoveSound(slotItem);
            return;
        }

        // Determine Cursor item
        final ItemStack cursorItem;
        if (actionType == SlotActionType.SWAP) {
            // Swap event
            if (PlayerInventory.isValidHotbarIndex(button)) {
                // Pressed hotbar key
                cursorItem = player.getInventory().getStack(button).copy();
            } else {
                // Pressed offhand key
                cursorItem = player.getOffHandStack().copy();
            }
        } else {
            cursorItem = cursor.copy();
        }

        if (slotIndex == ScreenHandler.EMPTY_SPACE_SLOT_INDEX && actionType != SlotActionType.QUICK_CRAFT) {
            // out of screen area
            if (RIGHT_CLICK_PREDICATE.test(actionType, button)) {
                cursorItem.setCount(1);
            }
            playThrow(cursorItem);
            return;
        }

        if (actionType == SlotActionType.THROW && button == 0) {
            // one item drop from stack (default: Q key)
            slotItem.setCount(1);
        }

        // Test if the item should not play sound
        {
            var predicateForCursor = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(cursorItem.getItem(), null);
            if (predicateForCursor != null && predicateForCursor.test(actionType, button)) {
                return;
            }
            var predicateForSlot = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(slotItem.getItem(), null);
            if (predicateForSlot != null && predicateForSlot.test(actionType, button)) {
                return;
            }
        }

        inventoryClick(slotItem, cursorItem, actionType);
    }

    public static SoundEvent getSoundOrThrow(ItemStack stack, SoundType type) throws NoSuchSoundException {
        var itemId = Registries.ITEM.getId(stack.getItem());
        Identifier id = ExtraSounds.getClickId(itemId, type);
        SoundEvent event = SoundPackLoader.CUSTOM_SOUND_EVENT.getOrDefault(id, null);
        if (event == null) {
            throw new NoSuchSoundException("Sound cannot be found in packs: " + id);
        }
        return event;
    }

    public static void playSound(ItemStack stack, SoundType type) {
        try {
            playSound(getSoundOrThrow(stack, type), type);
        } catch (NoSuchSoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public static void effectChanged(StatusEffect effect, EffectType type) {
        if (DebugUtils.DEBUG) {
            DebugUtils.effectLog(effect, type);
        }

        final SoundEvent event;
        if (type == EffectType.ADD) {
            event = switch (effect.getCategory()) {
                case HARMFUL -> Sounds.EFFECT_ADD_NEGATIVE;
                case NEUTRAL, BENEFICIAL -> Sounds.EFFECT_ADD_POSITIVE;
            };
        } else if (type == EffectType.REMOVE) {
            event = switch (effect.getCategory()) {
                case HARMFUL -> Sounds.EFFECT_REMOVE_NEGATIVE;
                case NEUTRAL, BENEFICIAL -> Sounds.EFFECT_REMOVE_POSITIVE;
            };
        } else {
            LOGGER.error("[{}] Unknown type of '{}' is approaching: '{}'", ExtraSounds.class.getSimpleName(), EffectType.class.getSimpleName(), type);
            return;
        }
        playSound(event, SoundType.EFFECT);
    }

    public static void playSound(SoundEvent snd, SoundType type) {
        playSound(snd, type.pitch, type.category);
    }

    public static void playSound(SoundEvent snd, float pitch, SoundCategory category, SoundCategory... optionalVolumes) {
        float volume = getSoundVolume(Mixers.MASTER);
        if (optionalVolumes != null) {
            for (SoundCategory cat : optionalVolumes) {
                volume = Math.min(getSoundVolume(cat), volume);
            }
        }
        playSound(new PositionedSoundInstance(snd.getId(), category, volume, pitch, MC_RANDOM,
                false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D,
                true));
    }

    public static void playSound(SoundEvent snd, SoundType type, float volume, float pitch, BlockPos position) {
        playSound(new PositionedSoundInstance(snd, type.category, getSoundVolume(Mixers.MASTER) * volume, pitch,
                MC_RANDOM, position));
    }

    public static void actionSound(SoundEvent snd, BlockPos position) {
        SoundType action = SoundType.ACTION;
        playSound(snd, action, 1f, action.pitch, position);
    }

    public static void actionSound(ItemStack stack, BlockPos position) {
        SoundType action = SoundType.ACTION;
        try {
            playSound(getSoundOrThrow(stack, SoundType.PICKUP), action, 1f, action.pitch, position);
        } catch (NoSuchSoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    public static void playSound(SoundInstance instance) {
        try {
            long now = System.currentTimeMillis();
            if (now - lastPlayed > 5) {
                final MinecraftClient client = MinecraftClient.getInstance();
                client.send(() -> client.getSoundManager().play(instance));
                lastPlayed = now;
                if (DebugUtils.DEBUG) {
                    DebugUtils.soundLog(instance);
                }
            } else {
                if (DebugUtils.DEBUG) {
                    LOGGER.warn("Sound suppressed due to the fast interval between method calls, was '{}'.", instance.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to play sound", e);
        }
    }

    public static void playThrow(ItemStack itemStack) {
        playThrow(itemStack, Mixers.INVENTORY);
    }

    /**
     * Plays the weighted THROW sound.<br>
     * The pitch is clamped between 1.5 - 2.0. The smaller stack, the higher.<br>
     * If the ItemStack is not stackable, the pitch is maximum.
     *
     * @param itemStack target stack to adjust the pitch.
     * @param category  SoundCategory to adjust the volume.
     * @see MathHelper#clampedLerp
     * @see net.minecraft.client.sound.SoundSystem#play
     * @see net.minecraft.client.sound.SoundSystem#getAdjustedPitch
     */
    public static void playThrow(ItemStack itemStack, SoundCategory category) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float maxPitch = 2f;
        final float pitch = (!itemStack.isStackable()) ? maxPitch :
                MathHelper.clampedLerp(maxPitch, 1.5f, (float) itemStack.getCount() / itemStack.getItem().getMaxCount());
        playSound(Sounds.ITEM_DROP, pitch, category, Mixers.ITEM_DROP);
    }

    public static void stopSound(SoundEvent e, SoundType type) {
        MinecraftClient.getInstance().getSoundManager().stopSounds(e.getId(), type.category);
    }

    /**
     * SlotActionType.QUICK_MOVE is too many method calls
     *
     * @param itemStack target item to quickMove
     * @see net.minecraft.client.network.ClientPlayerInteractionManager#clickSlot
     * @see net.minecraft.screen.ScreenHandler#internalOnSlotClick
     */
    public static void handleQuickMoveSound(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - lastPlayed > 50 || !itemStack.isOf(quickMovingItem)) {
            playSound(itemStack, SoundType.PICKUP);
            lastPlayed = now;
            quickMovingItem = itemStack.getItem();
        }
    }

    public static void keyboard(KeyType type) {
        switch (type) {
            case ERASE -> playSound(Sounds.KEYBOARD_ERASE, SoundType.TYPING);
            case CUT -> playSound(Sounds.KEYBOARD_CUT, SoundType.TYPING);
            case CURSOR, RETURN -> playSound(Sounds.KEYBOARD_MOVE, SoundType.TYPING);
            case INSERT -> playSound(Sounds.KEYBOARD_TYPE, SoundType.TYPING);
            case PASTE -> playSound(Sounds.KEYBOARD_PASTE, SoundType.TYPING);
        }
    }

    public static float getSoundVolume(SoundCategory category) {
        return MinecraftClient.getInstance().options.getSoundVolume(category);
    }
}
