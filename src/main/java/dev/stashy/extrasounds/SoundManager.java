package dev.stashy.extrasounds;

import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static long lastPlayed = System.currentTimeMillis();

    public static void playSound(ItemStack stack, SoundType type)
    {
        var itemId = Registries.ITEM.getId(stack.getItem());
        String idString = ExtraSounds.getClickId(itemId, type);
        if (!Identifier.isValid(idString))
        {
            LOGGER.error("Unable to parse sound from ID: " + idString);
            return;
        }

        Identifier id = Identifier.tryParse(idString);
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

    public static void stopSound(SoundEvent e, SoundType type)
    {
        MinecraftClient.getInstance().getSoundManager().stopSounds(e.getId(), type.category);
    }

    private static void throttle(Runnable r)
    {
        try
        {
            long now = System.currentTimeMillis();
            if (now - lastPlayed > 5) r.run();
            lastPlayed = now;
        }
        catch (Exception e)
        {
            System.err.println("Failed to play sound:");
            e.printStackTrace();
        }
    }

    private static float getMasterVol()
    {
        return MinecraftClient.getInstance().options.getSoundVolume(Mixers.MASTER);
    }
}
