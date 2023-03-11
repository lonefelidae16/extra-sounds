package dev.stashy.extrasounds;

import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager
{
    private static final Logger LOGGER = LogManager.getLogger();
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

    public static void playSound(ItemStack stack, SoundType type)
    {
        var itemId = Registries.ITEM.getId(stack.getItem());
        Identifier id = ExtraSounds.getClickId(itemId, type);
        SoundEvent event = SoundPackLoader.CUSTOM_SOUND_EVENT.getOrDefault(id, null);
        if (event == null) {
            LOGGER.error("Sound cannot be found in packs: {}", id);
            return;
        }
        playSound(event, type);
    }

    public static void playSound(StatusEffect effect, boolean add)
    {
        DebugUtils.effectLog(effect, add);

        SoundEvent e = add ?
                switch (effect.getCategory())
                        {
                            case HARMFUL -> Sounds.EFFECT_ADD_NEGATIVE;
                            case NEUTRAL, BENEFICIAL -> Sounds.EFFECT_ADD_POSITIVE;
                        }
                :
                switch (effect.getCategory())
                        {
                            case HARMFUL -> Sounds.EFFECT_REMOVE_NEGATIVE;
                            case NEUTRAL, BENEFICIAL -> Sounds.EFFECT_REMOVE_POSITIVE;
                        };
        playSound(e, SoundType.EFFECT);
    }

    public static void playSound(SoundEvent snd, SoundType type)
    {
        playSound(snd, type, type.category);
    }

    public static void playSound(SoundEvent snd, SoundType type, SoundCategory cat)
    {
        playSound(snd, type.pitch, cat);
    }

    public static void playSound(SoundEvent snd, float pitch, SoundCategory cat)
    {
        playSound(new PositionedSoundInstance(snd.getId(), cat, getMasterVol(), pitch, ExtraSounds.mcRandom,
                                              false, 0, SoundInstance.AttenuationType.NONE, 0.0D, 0.0D, 0.0D,
                                              true));
        DebugUtils.soundLog(snd);
    }

    public static void playSound(SoundEvent snd, SoundType type, BlockPos position)
    {
        playSound(new PositionedSoundInstance(snd, type.category, getMasterVol(), type.pitch,
                                              ExtraSounds.mcRandom,
                                              position.getX() + 0.5,
                                              position.getY() + 0.5,
                                              position.getZ() + 0.5));
        DebugUtils.soundLog(snd);
    }

    public static void playSound(PositionedSoundInstance instance)
    {
        throttle(() -> {
            var client = MinecraftClient.getInstance();
            client.send(() -> {
                client.getSoundManager().play(instance);
            });
        });
    }

    /**
     * Plays the weighted THROW sound.<br>
     * The pitch is clamped between 1.5 - 2.0. The smaller stack, the higher.<br>
     * If the ItemStack is not stackable, the pitch is maximum.
     * @param itemStack target stack to adjust the pitch.
     *
     * @see MathHelper#clampedLerp
     * @see net.minecraft.client.sound.SoundSystem#play
     * @see net.minecraft.client.sound.SoundSystem#getAdjustedPitch
     */
    public static void playThrow(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }
        final float maxPitch = 2f;
        final float pitch = (!itemStack.isStackable()) ? maxPitch :
                MathHelper.clampedLerp(maxPitch, 1.5f, (float) itemStack.getCount() / itemStack.getItem().getMaxCount());
        SoundManager.playSound(Sounds.ITEM_DROP, pitch, Mixers.INVENTORY);
    }

    public static void stopSound(SoundEvent e, SoundType type)
    {
        MinecraftClient.getInstance().getSoundManager().stopSounds(e.getId(), type.category);
    }

    /**
     * SlotActionType.QUICK_MOVE is too many method calls
     * @param itemStack target item to quickMove
     *
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

    private static void throttle(Runnable r)
    {
        try
        {
            long now = System.currentTimeMillis();
            if (now - lastPlayed > 5) r.run();
            lastPlayed = now;
        }
        catch (Throwable e)
        {
            LOGGER.error("Failed to play sound", e);
        }
    }

    private static float getMasterVol()
    {
        return MinecraftClient.getInstance().options.getSoundVolume(Mixers.MASTER);
    }
}
