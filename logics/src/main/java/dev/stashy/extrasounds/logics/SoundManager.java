package dev.stashy.extrasounds.logics;

import com.google.common.collect.Sets;
import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.entry.SoundPackLoader;
import dev.stashy.extrasounds.logics.impl.HotbarSoundHandler;
import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.runtime.VersionedPositionedSoundInstanceWrapper;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import dev.stashy.extrasounds.logics.throwable.SoundNotFoundException;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.Set;

public final class SoundManager {
    private static final Logger LOGGER = LogManager.getLogger(
            SoundManager.class,
            new PrefixableMessageFactory("%s/%s".formatted(
                    ExtraSounds.class.getSimpleName(),
                    SoundManager.class.getSimpleName()
            ))
    );

    public static final VersionedSoundEventWrapper FALLBACK_SOUND_EVENT = Sounds.ITEM_PICK;

    private final HotbarSoundHandler hotbarSoundHandler;
    private final Set<Identifier> missingSoundId;
    private long lastPlayed;
    private Item quickMovingItem;

    public SoundManager() {
        this.hotbarSoundHandler = new HotbarSoundHandler();
        this.missingSoundId = Sets.newHashSet();
        this.lastPlayed = 0;
        this.quickMovingItem = Items.AIR;
    }

    /**
     * Handles Click and KeyPress on inventory
     *
     * @param player player instance
     * @param state  click state
     */
    public void handleInventorySlot(PlayerEntity player, InventoryClickState state) {
        final SlotActionType actionType = state.actionType;

        if (state.isQuickCrafting()) {
            // while dragging.
            return;
        }
        if (state.slotIndex == -1) {
            // screen border clicked.
            return;
        }

        // Determine Slot item.
        final ItemStack slotStack = state.getSlotStack();
        if (actionType == SlotActionType.QUICK_MOVE) {
            // cursor holding an item, then Shift + mouse (double) click.
            this.handleQuickMoveSound(slotStack.getItem());
            return;
        }
        if (state.isSlotBlocked()) {
            // cannot insert.
            return;
        }

        // Determine Cursor item.
        final ItemStack cursorStack = state.getCursorStack(player);

        final boolean hasCursor = !cursorStack.isEmpty();
        final boolean hasSlot = !slotStack.isEmpty();
        if (!hasCursor && !hasSlot) {
            // Early return when both are empty.
            return;
        }

        if (state.isEmptySpaceClicked()) {
            // Out of screen area.
            if (state.isRMB) {
                cursorStack.setCount(1);
            }
            this.playThrow(cursorStack);
            return;
        }

        // Test if the item should not play sound.
        if (ExtraSounds.MAIN.shouldIgnoreItemSound(cursorStack.getItem(), slotStack.getItem(), state)) {
            return;
        }

        switch (actionType) {
            case PICKUP_ALL -> {
                if (hasCursor) {
                    this.playSound(Sounds.ITEM_PICK_ALL, SoundType.PICKUP);
                }
            }
            case THROW -> {
                if (!hasCursor) {
                    if (state.button == 0) {
                        // one item drop from stack (default: Q key)
                        slotStack.setCount(1);
                    }
                    this.playThrow(slotStack);
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
                if (!hasSlot || hasCursor && ExtraSounds.canItemsCombine(slotStack, cursorStack)) {
                    this.playSound(cursorStack.getItem(), SoundType.PLACE);
                } else {
                    this.playSound(slotStack.getItem(), SoundType.PICKUP);
                }
            }
        }
    }

    public void hotbar(int i) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }

        if (!PlayerInventory.isValidHotbarIndex(i)) {
            LOGGER.error("Invalid index '{}' was passed.", i, new IndexOutOfBoundsException(i));
            return;
        }

        ItemStack stack = player.getInventory().getStack(i);
        if (stack.isEmpty()) {
            this.playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
        } else {
            this.playSound(stack.getItem(), SoundType.HOTBAR);
        }
    }

    public void blockInteract(VersionedSoundEventWrapper snd, BlockPos position) {
        SoundType blockIntr = SoundType.BLOCK_INTR;
        this.playSound(snd, blockIntr, 1f, blockIntr.pitch, position);
    }

    public void blockInteract(Item item, BlockPos position) {
        this.blockInteract(this.getSoundByItem(item, SoundType.PICKUP), position);
    }

    public void playSound(VersionedSoundEventWrapper snd, SoundType type) {
        this.playSound(snd, type.pitch, type.category);
    }

    public void playSound(Item item, SoundType type) {
        this.playSound(this.getSoundByItem(item, type), type.pitch, type.category);
    }

    /**
     * SlotActionType.QUICK_MOVE is too many method calls
     *
     * @param item Target item to quickMove
     * @see net.minecraft.client.network.ClientPlayerInteractionManager#clickSlot
     * @see ScreenHandler#internalOnSlotClick
     */
    private void handleQuickMoveSound(Item item) {
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

    public void playSound(VersionedSoundEventWrapper snd, float pitch, SoundCategory category, SoundCategory... optionalVolumes) {
        float volume = this.getSoundVolume(Mixers.MASTER);
        if (optionalVolumes != null) {
            for (SoundCategory cat : optionalVolumes) {
                volume = Math.min(this.getSoundVolume(cat), volume);
            }
        }
        if (volume == 0 || this.isMuted(category)) {
            // skip reflection when volume is zero.
            if (DebugUtils.DEBUG) {
                this.logZeroVolume(snd);
            }
            return;
        }
        final var soundInstance = VersionedPositionedSoundInstanceWrapper.newInstance(
                snd.getId(), category, volume, pitch, false, 0, SoundInstance.AttenuationType.NONE,
                0.0D, 0.0D, 0.0D, true
        );
        this.playSound(Objects.requireNonNull(soundInstance));
    }

    public void playSound(VersionedSoundEventWrapper snd, SoundType type, float volume, float pitch, BlockPos position) {
        volume *= this.getSoundVolume(Mixers.MASTER);
        if (volume == 0 || this.isMuted(type.category)) {
            // skip reflection when volume is zero.
            if (DebugUtils.DEBUG) {
                this.logZeroVolume(snd);
            }
            return;
        }
        final var soundInstance = VersionedPositionedSoundInstanceWrapper.newInstance(
                snd, type.category, volume, pitch, position
        );
        this.playSound(Objects.requireNonNull(soundInstance));
    }

    public boolean isMuted(SoundType type) {
        return this.isMuted(type.category);
    }

    private boolean isMuted(SoundCategory category) {
        return this.getSoundVolume(category) == 0;
    }

    private void logZeroVolume(VersionedSoundEventWrapper snd) {
        LOGGER.warn("Sound suppressed due to zero volume, was '{}'.", snd.getId());
    }

    private void playSound(SoundInstance instance) {
        try {
            long now = System.currentTimeMillis();
            if (now - this.lastPlayed > 5) {
                ExtraSounds.MAIN.playSound(instance);
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
     * @see MathHelper#lerp
     * @see net.minecraft.client.sound.SoundSystem#play
     * @see net.minecraft.client.sound.SoundSystem#getAdjustedPitch
     */
    public void playThrow(ItemStack itemStack, SoundCategory category) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float maxPitch = 2f;
        final float pitch = (!itemStack.isStackable()) ? maxPitch :
                MathHelper.lerp((itemStack.getCount() - 1f) / (itemStack.getItem().getMaxCount() - 1f), maxPitch, 1.5f);
        this.playSound(Sounds.ITEM_DROP, pitch, category, Mixers.ITEM_DROP);
    }

    public void stopSound(VersionedSoundEventWrapper e, SoundType type) {
        MinecraftClient.getInstance().getSoundManager().stopSounds(e.getId(), type.category);
    }

    private float getSoundVolume(SoundCategory category) {
        return MinecraftClient.getInstance().options.getSoundVolume(category);
    }

    public VersionedSoundEventWrapper getSoundByItem(Item item, SoundType type) {
        var itemId = ExtraSounds.getItemId(item);
        Identifier id = ExtraSounds.getClickId(itemId, type);
        VersionedSoundEventWrapper sound = SoundPackLoader.CUSTOM_SOUND_EVENT.getOrDefault(id, null);
        if (sound == null) {
            if (!this.missingSoundId.contains(id)) {
                this.missingSoundId.add(id);
                LOGGER.error("Sound '{}' cannot be found in packs.", id, new SoundNotFoundException());
            }
            return FALLBACK_SOUND_EVENT;
        }
        return sound;
    }

    public HotbarSoundHandler getHotbarSoundHandler() {
        return this.hotbarSoundHandler;
    }
}
