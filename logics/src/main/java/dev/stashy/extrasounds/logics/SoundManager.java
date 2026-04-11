package dev.stashy.extrasounds.logics;

import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.entry.SoundPackLoader;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import dev.stashy.extrasounds.logics.impl.state.InventoryClickState;
import dev.stashy.extrasounds.logics.impl.state.SlotActionType;
import dev.stashy.extrasounds.logics.runtime.VersionedPositionedSoundInstanceWrapper;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
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

    private final VersionedHotbarSoundHandler hotbarSoundHandler;
    private final Set<Identifier> missingSoundId;
    private long lastPlayed;
    private Item quickMovingItem;

    public SoundManager() {
        this.hotbarSoundHandler = VersionedHotbarSoundHandler.newInstance();
        this.missingSoundId = new HashSet<>();
        this.lastPlayed = 0;
        this.quickMovingItem = Items.AIR;
    }

    /**
     * Handles Click and KeyPress on inventory
     *
     * @param player player instance
     * @param state  click state
     */
    public void handleInventorySlot(Player player, InventoryClickState state) {
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
            this.handleQuickMoveSound(slotStack);
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
                    this.playSound2D(Sounds.ITEM_PICK_ALL, SoundType.GRAB);
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
                if (!hasSlot || hasCursor && ExtraSounds.MAIN.canItemsCombine(slotStack, cursorStack)) {
                    this.playSound2D(cursorStack, SoundType.PLACE);
                } else {
                    this.playSound2D(slotStack, SoundType.GRAB);
                }
            }
        }
    }

    public void hotbar(int i) {
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        if (!Inventory.isHotbarSlot(i)) {
            LOGGER.error("Invalid index '{}' was passed.", i, new IndexOutOfBoundsException(i));
            return;
        }

        ItemStack stack = player.getInventory().getItem(i).copy();
        if (stack.isEmpty()) {
            this.playSound2D(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
        } else {
            this.playSound2D(stack, SoundType.HOTBAR);
        }
    }

    public void blockInteract(VersionedSoundEventWrapper snd, BlockPos position) {
        SoundType blockIntr = SoundType.BLOCK_INTR;
        this.playSound(snd, blockIntr.category, 1f, blockIntr.pitch, position);
    }

    public void blockInteract(Item item, BlockPos position) {
        this.blockInteract(this.getSoundByItem(item.getDefaultInstance(), SoundType.DEFAULT), position);
    }

    public void playSound2D(VersionedSoundEventWrapper snd, SoundType type) {
        this.playSound2D(snd, type.category, type.pitch);
    }

    public void playSound2D(ItemStack item, SoundType type) {
        this.playSound2D(this.getSoundByItem(item, type), type.category, type.pitch);
    }

    /**
     * SlotActionType.QUICK_MOVE is too many method calls
     *
     * @param itemStack Target ItemStack to quickMove
     * @see net.minecraft.client.multiplayer.MultiPlayerGameMode#handleContainerInput
     * @see net.minecraft.world.inventory.AbstractContainerMenu#doClick
     */
    private void handleQuickMoveSound(ItemStack itemStack) {
        final Item item = itemStack.getItem();
        if (item == Items.AIR) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastPlayed > 10 || item != this.quickMovingItem) {
            this.playSound2D(itemStack, SoundType.GRAB);
            this.lastPlayed = now;
            this.quickMovingItem = item;
        }
    }

    public void playSound2D(VersionedSoundEventWrapper snd, SoundSource category) {
        this.playSound2D(snd, category, 1);
    }

    public void playSound2D(VersionedSoundEventWrapper snd, SoundSource category, float pitch, SoundSource... optionalVolumes) {
        float volume = 1;
        if (optionalVolumes != null) {
            for (SoundSource cat : optionalVolumes) {
                volume = Math.min(ExtraSounds.MAIN.getSoundVolume(cat), volume);
            }
        }
        this.playSound(snd, category, volume, pitch, null);
    }

    public void playSound(VersionedSoundEventWrapper snd, SoundSource category, float volume, float pitch, @Nullable BlockPos position) {
        volume *= ExtraSounds.MAIN.getSoundVolume(Mixers.MASTER);
        if (volume == 0 || this.isMuted(category)) {
            // skip reflection when volume is zero.
            if (DebugUtils.DEBUG) {
                this.logZeroVolume(snd);
            }
            return;
        }
        final VersionedPositionedSoundInstanceWrapper soundInstance;
        if (position == null) {
            soundInstance = VersionedPositionedSoundInstanceWrapper.newInstance(
                    snd.getId(), category, volume, pitch, false, 0, SoundInstance.Attenuation.NONE,
                    0.0D, 0.0D, 0.0D, true
            );
        } else {
            soundInstance = VersionedPositionedSoundInstanceWrapper.newInstance(
                    snd.getId(), category, volume, pitch, false, 0, SoundInstance.Attenuation.LINEAR,
                    position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5, false
            );
        }
        this.playSound(Objects.requireNonNull(soundInstance));
    }

    public boolean isMuted(SoundType type) {
        return this.isMuted(type.category);
    }

    private boolean isMuted(SoundSource category) {
        return ExtraSounds.MAIN.getSoundVolume(category) == 0;
    }

    private void logZeroVolume(VersionedSoundEventWrapper snd) {
        LOGGER.warn("Sound '{}' was suppressed: zero volume.", snd.getId());
    }

    private void playSound(SoundInstance instance) {
        try {
            long now = System.currentTimeMillis();
            if (now - this.lastPlayed > 5) {
                ExtraSounds.MAIN.playSound(instance);
                this.lastPlayed = now;
                if (DebugUtils.DEBUG) {
                    LOGGER.info("Playing sound: {}", instance.getIdentifier());
                }
            } else {
                if (DebugUtils.DEBUG) {
                    LOGGER.warn("Sound '{}' was suppressed: fast interval between method calls.", instance.getIdentifier());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to play sound.", e);
        }
    }

    public void playThrow(ItemStack itemStack) {
        this.playThrow(itemStack, Mixers.SCREENS);
    }

    /**
     * Plays the weighted THROW sound.<br>
     * The pitch is clamped between 1.5 - 2.0. The smaller stack, the higher.<br>
     * If an ItemStack is not stackable, the pitch is maximum.
     *
     * @param itemStack Target stack to adjust the pitch.
     * @param category  {@link SoundSource} to adjust the volume.
     * @see Mth#lerp
     * @see net.minecraft.client.sounds.SoundEngine#play
     * @see net.minecraft.client.sounds.SoundEngine#calculatePitch
     */
    public void playThrow(ItemStack itemStack, SoundSource category) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float maxPitch = 2f;
        final float pitch = (!itemStack.isStackable()) ? maxPitch :
                Mth.lerp((itemStack.getCount() - 1f) / (itemStack.getItem().getDefaultMaxStackSize() - 1f), maxPitch, 1.5f);
        this.playSound2D(Sounds.ITEM_DROP, category, pitch, Mixers.ITEM_DROP);
    }

    public void stopSound(VersionedSoundEventWrapper e, SoundType type) {
        ExtraSounds.MAIN.stopSound(e, type);
    }

    public VersionedSoundEventWrapper getSoundByItem(ItemStack item, SoundType type) {
        final Identifier[] itemIds = ExtraSounds.MAIN.getItemIdWithComponents(item, DataComponents.ITEM_MODEL);
        for (int i = 0; i < itemIds.length; ++i) {
            itemIds[i] = ExtraSounds.getClickId(itemIds[i], type);
        }
        final var sound = SoundPackLoader.getSoundEventById(itemIds);
        if (sound.isEmpty()) {
            logMissingSoundIds(itemIds);
            return FALLBACK_SOUND_EVENT;
        } else {
            return sound.get();
        }
    }

    private void logMissingSoundIds(Identifier... ids) {
        for (Identifier id : ids) {
            if (!this.missingSoundId.contains(id)) {
                this.missingSoundId.add(id);
                LOGGER.error("Sound '{}' cannot be found in packs.", id);
            }
        }
    }

    public VersionedHotbarSoundHandler getHotbarSoundHandler() {
        return this.hotbarSoundHandler;
    }
}
