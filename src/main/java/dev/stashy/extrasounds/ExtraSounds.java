package dev.stashy.extrasounds;

import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

public class ExtraSounds implements ClientModInitializer
{
    public static final String MODID = "extrasounds";
    static final Random mcRandom = Random.create();

    @Override
    public void onInitializeClient()
    {
        SoundPackLoader.init();
        DebugUtils.init();
    }

    public static void hotbar(int i)
    {
        ItemStack stack = MinecraftClient.getInstance().player.getInventory().getStack(i);
        if (stack.getItem() == Items.AIR)
            SoundManager.playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
        else
            SoundManager.playSound(stack, SoundType.HOTBAR);
    }

    public static void inventoryClick(Slot slot, ItemStack cursor, SlotActionType actionType)
    {
        ItemStack clicked = slot.getStack();
        boolean hasCursor = !cursor.isEmpty();
        boolean hasSlot = !clicked.isEmpty();

        switch (actionType)
        {
            case PICKUP_ALL:
                if (hasCursor)
                    SoundManager.playSound(Sounds.ITEM_PICK_ALL, SoundType.PICKUP);
                return;
            case CLONE:
                SoundManager.playSound(Sounds.ITEM_CLONE, SoundType.PLACE);
                return;
            default:
                if (hasCursor) SoundManager.playSound(cursor, SoundType.PICKUP);
                else if (hasSlot)
                    SoundManager.playSound(clicked, SoundType.PLACE);
        }
    }

    public static String getClickId(Identifier id, SoundType type)
    {
        return getClickId(id, type, true);
    }

    public static String getClickId(Identifier id, SoundType type, boolean includeNamespace)
    {
        return (includeNamespace ? MODID + ":" : "") + type.prefix + "." + id.getNamespace() + "." + id.getPath();
    }
}