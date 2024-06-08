package dev.stashy.extrasounds.logics;

import com.google.common.collect.Maps;
import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.mapping.SoundPackLoader;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import dev.stashy.extrasounds.logics.sounds.VersionedPositionedSoundInstanceWrapper;
import dev.stashy.extrasounds.logics.throwable.NoSuchSoundException;
import me.lonefelidae16.groominglib.Util;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

public abstract class VersionedSoundManager {
    protected static final Logger LOGGER = LogManager.getLogger(
            VersionedSoundManager.class,
            new PrefixableMessageFactory("%s/%s".formatted(
                    ExtraSounds.class.getSimpleName(),
                    VersionedSoundManager.class.getSimpleName()
            ))
    );

    public static final SoundEvent FALLBACK_SOUND_EVENT = Sounds.ITEM_PICK;

    /**
     * Predicate of Right Mouse Click.
     */
    public static final BiPredicate<SlotActionType, Integer> RIGHT_CLICK_PREDICATE = (actionType, button) -> {
        return (actionType != SlotActionType.THROW && actionType != SlotActionType.SWAP) && button == 1 ||
                actionType == SlotActionType.QUICK_CRAFT && ScreenHandler.unpackQuickCraftButton(button) == 1;
    };

    /**
     * Map of an item which should not play sounds.<br>
     * BiPredicate in this value will be passed <code>SlotActionType</code> and <code>int</code> of button ID.<br>
     * Item -&gt; BiPredicate&lt;SlotActionType, Integer&gt;
     */
    protected static final Map<Item, BiPredicate<SlotActionType, Integer>> IGNORE_SOUND_PREDICATE_MAP = Util.make(Maps.newHashMap(), map -> {
        map.put(Items.BUNDLE, RIGHT_CLICK_PREDICATE);
    });

    protected final List<Identifier> missingSoundId;
    protected long lastPlayed;
    protected Item quickMovingItem;

    protected VersionedSoundManager() {
        this.missingSoundId = Lists.newArrayList();
        this.lastPlayed = 0;
        this.quickMovingItem = Items.AIR;
    }

    public static VersionedSoundManager newInstance() {
        try {
            Class<VersionedSoundManager> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "SoundManager");
            Constructor<VersionedSoundManager> init = clazz.getConstructor();
            return init.newInstance();
        } catch (Exception ex) {
            LOGGER.error("Cannot initialize 'SoundManager'", ex);
        }
        return null;
    }

    protected abstract boolean canItemsCombine(ItemStack stack1, ItemStack stack2);

    /**
     * Handles Click and KeyPress on inventory
     *
     * @param player     player instance
     * @param slot       slot in ScreenHandler
     * @param slotIndex  slotIndex
     * @param cursor     item that held by cursor
     * @param actionType action type
     * @param button     clicked mouse, pressed key or including {@code QuickCraftStage}
     */
    public void handleInventorySlot(PlayerEntity player, @Nullable Slot slot, int slotIndex, ItemStack cursor, SlotActionType actionType, int button) {
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
            this.handleQuickMoveSound(slotItem.getItem());
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

        final boolean hasCursor = !cursorItem.isEmpty();
        final boolean hasSlot = !slotItem.isEmpty();
        if (!hasCursor && !hasSlot) {
            // Early return when both are empty.
            return;
        }

        if (slotIndex == ScreenHandler.EMPTY_SPACE_SLOT_INDEX && actionType != SlotActionType.QUICK_CRAFT) {
            // out of screen area
            if (RIGHT_CLICK_PREDICATE.test(actionType, button)) {
                cursorItem.setCount(1);
            }
            this.playThrow(cursorItem);
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

        switch (actionType) {
            case PICKUP_ALL -> {
                if (hasCursor) {
                    this.playSound(Sounds.ITEM_PICK_ALL, SoundType.PICKUP);
                }
            }
            case THROW -> {
                if (!hasCursor) {
                    this.playThrow(slotItem);
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
                if (!hasSlot || hasCursor && this.canItemsCombine(slotItem, cursorItem)) {
                    this.playSound(cursorItem.getItem(), SoundType.PLACE);
                } else {
                    this.playSound(slotItem.getItem(), SoundType.PICKUP);
                }
            }
        }
    }

    public void hotbar(int i) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getInventory().getStack(i);
        if (stack.getItem() == Items.AIR) {
            this.playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
        } else {
            this.playSound(stack.getItem(), SoundType.HOTBAR);
        }
    }

    public void blockInteract(SoundEvent snd, BlockPos position) {
        SoundType blockIntr = SoundType.BLOCK_INTR;
        this.playSound(snd, blockIntr, 1f, blockIntr.pitch, position);
    }

    public void blockInteract(Item item, BlockPos position) {
        this.blockInteract(getSoundByItem(item, SoundType.PICKUP), position);
    }

    public void playSound(SoundEvent snd, SoundType type) {
        this.playSound(snd, type.pitch, type.category);
    }

    public void playSound(Item item, SoundType type) {
        this.playSound(getSoundByItem(item, type), type.pitch, type.category);
    }

    /**
     * SlotActionType.QUICK_MOVE is too many method calls
     *
     * @param item Target item to quickMove
     * @see net.minecraft.client.network.ClientPlayerInteractionManager#clickSlot
     * @see ScreenHandler#internalOnSlotClick
     */
    protected void handleQuickMoveSound(Item item) {
        if (item == Items.AIR) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastPlayed > 10 || item != this.quickMovingItem) {
            this.playSound(item, SoundType.PICKUP);
            this.lastPlayed = now;
            this.quickMovingItem = item;
        }
    }

    public void playSound(SoundEvent snd, float pitch, SoundCategory category, SoundCategory... optionalVolumes) {
        float volume = getSoundVolume(Mixers.MASTER);
        if (optionalVolumes != null) {
            for (SoundCategory cat : optionalVolumes) {
                volume = Math.min(getSoundVolume(cat), volume);
            }
        }
        this.playSound(
                (PositionedSoundInstance) Objects.requireNonNull(
                        VersionedPositionedSoundInstanceWrapper.newInstance(snd.getId(), category, volume, pitch,
                                false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D,
                                true)
                )
        );
    }

    public void playSound(SoundEvent snd, SoundType type, float volume, float pitch, BlockPos position) {
        this.playSound(
                (PositionedSoundInstance) Objects.requireNonNull(
                        VersionedPositionedSoundInstanceWrapper.newInstance(snd, type.category, getSoundVolume(Mixers.MASTER) * volume, pitch,
                                position)
                )
        );
    }

    protected void playSound(SoundInstance instance) {
        try {
            long now = System.currentTimeMillis();
            if (now - this.lastPlayed > 5) {
                final MinecraftClient client = MinecraftClient.getInstance();
                client.send(() -> client.getSoundManager().play(instance));
                this.lastPlayed = now;
                if (DebugUtils.DEBUG) {
                    LOGGER.info("Playing sound: {}", instance.getId());
                }
            } else {
                if (DebugUtils.DEBUG) {
                    LOGGER.warn("Sound suppressed due to the fast interval between method calls, was '{}'.", instance.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to play sound.", e);
        }
    }

    public void playThrow(ItemStack itemStack) {
        this.playThrow(itemStack, Mixers.INVENTORY);
    }

    /**
     * Plays the weighted THROW sound.<br>
     * The pitch is clamped between 1.5 - 2.0. The smaller stack, the higher.<br>
     * If an ItemStack is not stackable, the pitch is maximum.
     *
     * @param itemStack Target stack to adjust the pitch.
     * @param category  {@link SoundCategory} to adjust the volume.
     * @see MathHelper#clampedLerp
     * @see net.minecraft.client.sound.SoundSystem#play
     * @see net.minecraft.client.sound.SoundSystem#getAdjustedPitch
     */
    public void playThrow(ItemStack itemStack, SoundCategory category) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float maxPitch = 2f;
        final float pitch = (!itemStack.isStackable()) ? maxPitch :
                MathHelper.clampedLerp(maxPitch, 1.5f, (float) itemStack.getCount() / itemStack.getItem().getMaxCount());
        this.playSound(Sounds.ITEM_DROP, pitch, category, Mixers.ITEM_DROP);
    }

    public void stopSound(SoundEvent e, SoundType type) {
        MinecraftClient.getInstance().getSoundManager().stopSounds(e.getId(), type.category);
    }

    protected float getSoundVolume(SoundCategory category) {
        return MinecraftClient.getInstance().options.getSoundVolume(category);
    }

    protected SoundEvent getSoundByItem(Item item, SoundType type) {
        var itemId = ExtraSounds.fromItemRegistry(item);
        Identifier id = ExtraSounds.getClickId(itemId, type);
        SoundEvent sound = SoundPackLoader.CUSTOM_SOUND_EVENT.getOrDefault(id, null);
        if (sound == null) {
            if (!this.missingSoundId.contains(id)) {
                this.missingSoundId.add(id);
                LOGGER.error("Sound '{}' cannot be found in packs.", id, new NoSuchSoundException());
            }
            return FALLBACK_SOUND_EVENT;
        }
        return sound;
    }
}
