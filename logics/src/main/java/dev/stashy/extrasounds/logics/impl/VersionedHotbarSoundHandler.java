package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.Mixers;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class VersionedHotbarSoundHandler {
    public static final int FORCE_HOTBAR_CHANGE = -1;

    private static final Item ITEM_EMPTY = Items.AIR;

    private Item pickingItem = ITEM_EMPTY;

    public abstract int getPlayerInventorySlot(Player player);

    public static VersionedHotbarSoundHandler newInstance() {
        try {
            Class<VersionedHotbarSoundHandler> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "impl.HotbarSoundHandler");
            return clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Failed to initialize 'HotbarSoundHandler'", ex);
        }
        return null;
    }

    public void onSwap(ItemStack mainHand, ItemStack offHand) {
        if (offHand.getItem() != ITEM_EMPTY) {
            ExtraSounds.MANAGER.playSound2D(offHand, SoundType.HOTBAR);
        } else if (mainHand.getItem() != ITEM_EMPTY) {
            ExtraSounds.MANAGER.playSound2D(mainHand, SoundType.HOTBAR);
        }
    }

    public void onChange() {
        this.onChange(FORCE_HOTBAR_CHANGE);
    }

    public void onChange(int newSlot) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        final int target = (newSlot == FORCE_HOTBAR_CHANGE) ? this.getPlayerInventorySlot(player) : newSlot;
        ExtraSounds.MANAGER.hotbar(target);
    }

    public void spectatorHotbar() {
        ExtraSounds.MANAGER.playSound2D(Sounds.HOTBAR_SCROLL, SoundType.HOTBAR);
    }

    public void doItemPick(Item item) {
        this.storePickingItem(item);
        this.onItemPick();
    }

    public void storePickingItem(Item item) {
        this.setPickingItem(item);
    }

    public void onItemPick() {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        final Item item = this.popPickingItem();
        if (!player.getMainHandItem().is(item) && item != ITEM_EMPTY) {
            ExtraSounds.MANAGER.playSound2D(item.getDefaultInstance(), SoundType.HOTBAR);
        }
    }

    public void setPickingItem(Item item) {
        this.pickingItem = item;
    }

    public Item popPickingItem() {
        final Item result = this.pickingItem;
        this.pickingItem = ITEM_EMPTY;
        return result;
    }

    public void onThrow(ItemStack itemStack) {
        ExtraSounds.MANAGER.playThrow(itemStack, Mixers.HOTBAR);
    }
}
