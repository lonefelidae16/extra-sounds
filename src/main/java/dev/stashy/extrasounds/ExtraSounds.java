package dev.stashy.extrasounds;

import com.google.common.collect.Maps;
import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiPredicate;

public class ExtraSounds implements ClientModInitializer {
    public static final String MODID = "extrasounds";
    static final Random mcRandom = Random.create();

    /**
     * Predicate of Right Mouse Click.
     */
    private static final BiPredicate<SlotActionType, Integer> RIGHT_CLICK_PREDICATE = (actionType, button) -> {
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

    @Override
    public void onInitializeClient()
    {
        SoundPackLoader.init();
        DebugUtils.init();
    }

    public static void hotbar(int i) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        ItemStack stack = player.getInventory().getStack(i);
        if (stack.getItem() == Items.AIR)
            SoundManager.playSound(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
        else
            SoundManager.playSound(stack, SoundType.HOTBAR);
    }

    public static void inventoryClick(ItemStack inSlot, ItemStack onCursor, SlotActionType actionType) {
        final boolean hasCursor = !onCursor.isEmpty();
        final boolean hasSlot = !inSlot.isEmpty();
        if (!hasCursor && !hasSlot) {
            return;
        }

        switch (actionType) {
            case PICKUP_ALL:
                if (hasCursor)
                    SoundManager.playSound(Sounds.ITEM_PICK_ALL, SoundType.PICKUP);
                return;
            case THROW:
                if (!hasCursor) {
                    SoundManager.playThrow(inSlot);
                }
                return;
            case QUICK_MOVE:
                if (hasSlot) {
                    SoundManager.handleQuickMoveSound(inSlot);
                }
                return;
            default:
                if (hasSlot) {
                    SoundManager.playSound(inSlot, SoundType.PICKUP);
                } else {
                    SoundManager.playSound(onCursor, SoundType.PLACE);
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
            SoundManager.handleQuickMoveSound(slotItem);
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

        if (slotIndex == -999 && actionType != SlotActionType.QUICK_CRAFT) {
            // out of screen area
            if (RIGHT_CLICK_PREDICATE.test(actionType, button)) {
                cursorItem.setCount(1);
            }
            SoundManager.playThrow(cursorItem);
            return;
        }

        if (actionType == SlotActionType.THROW && button == 0) {
            // one item drop from stack (default: Q key)
            slotItem.setCount(1);
        }

        // Test if the item should not play sound
        try {
            var predicateForCursor = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(cursorItem.getItem(), null);
            if (predicateForCursor != null && predicateForCursor.test(actionType, button)) {
                return;
            }
            var predicateForSlot = IGNORE_SOUND_PREDICATE_MAP.getOrDefault(slotItem.getItem(), null);
            if (predicateForSlot != null && predicateForSlot.test(actionType, button)) {
                return;
            }
        } catch (Throwable ignore) {
        }

        inventoryClick(slotItem, cursorItem, actionType);
    }

    public static String getClickId(Identifier id, SoundType type) {
        return getClickId(id, type, true);
    }

    public static String getClickId(Identifier id, SoundType type, boolean includeNamespace) {
        return (includeNamespace ? MODID + ":" : "") + type.prefix + "." + id.getNamespace() + "." + id.getPath();
    }
}