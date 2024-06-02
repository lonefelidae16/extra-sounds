package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.mutable.MutableObject;

public abstract class AbstractInteractionHandler {
    protected BlockState blockState;
    protected BlockEntity blockEntity;
    protected Block block;
    protected ItemStack currentHandStack;
    protected ItemStack mainHandStack;
    protected ItemStack offHandStack;

    protected abstract boolean canItemsCombine(ItemStack stack1, ItemStack stack2);

    protected abstract EquipmentSlot getPreferredSlot(ArmorStandEntity armorStandEntity, ItemStack itemStack);

    private boolean canInteractBlock(PlayerEntity player) {
        return !player.isSneaking() || (player.isSneaking() && this.mainHandStack.isEmpty() && this.offHandStack.isEmpty());
    }

    public final void setBlockStatus(BlockState blockState, BlockEntity blockEntity, ItemStack stackInHand, ItemStack mainHandStack, ItemStack offHandStack) {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
        this.block = blockState.getBlock();
        this.currentHandStack = stackInHand.copy();
        this.mainHandStack = mainHandStack.copy();
        this.offHandStack = offHandStack.copy();
    }

    public final void onUse(ClientPlayerEntity player, BlockPos blockPos, MutableObject<ActionResult> mutableObject) {
        if (this.blockState.isOf(Blocks.REPEATER) &&
                this.blockState.contains(RepeaterBlock.DELAY) &&
                this.canInteractBlock(player)
        ) {
            // Repeater
            final SoundEvent sound = this.blockState.get(RepeaterBlock.DELAY) == 4 ? Sounds.Actions.REPEATER_RESET : Sounds.Actions.REPEATER_ADD;
            ExtraSounds.MANAGER.blockInteract(sound, blockPos);
        } else if (this.blockState.isOf(Blocks.DAYLIGHT_DETECTOR) &&
                this.blockState.contains(DaylightDetectorBlock.INVERTED) &&
                this.canInteractBlock(player)
        ) {
            // Daylight Detector
            final SoundEvent sound = this.blockState.get(DaylightDetectorBlock.INVERTED) ? Sounds.Actions.REDSTONE_COMPONENT_ON : Sounds.Actions.REDSTONE_COMPONENT_OFF;
            ExtraSounds.MANAGER.blockInteract(sound, blockPos);
        } else if (this.blockState.isOf(Blocks.REDSTONE_WIRE) && this.canInteractBlock(player)) {
            // Redstone Wire
            ExtraSounds.MANAGER.blockInteract(Sounds.Actions.REDSTONE_WIRE_CHANGE, blockPos);
        } else if (this.blockState.isIn(BlockTags.REDSTONE_ORES) &&
                this.blockState.contains(RedstoneOreBlock.LIT) &&
                this.canInteractBlock(player) && !(this.mainHandStack.getItem() instanceof BlockItem)
        ) {
            // Redstone Ores
            ExtraSounds.MANAGER.blockInteract(this.block.asItem(), blockPos);
        } else if (this.blockState.isIn(BlockTags.CAMPFIRES) && (this.blockEntity instanceof CampfireBlockEntity campfireBlockEntity)) {
            // Put item on Campfire
            var recipe = campfireBlockEntity.getRecipeFor(this.currentHandStack);
            if (recipe.isPresent() && mutableObject.getValue() == ActionResult.CONSUME) {
                ExtraSounds.MANAGER.blockInteract(this.currentHandStack.getItem(), blockPos);
            }
        } else if (this.blockState.isIn(BlockTags.FLOWER_POTS) &&
                (this.block instanceof FlowerPotBlock potBlock) &&
                mutableObject.getValue() == ActionResult.SUCCESS
        ) {
            if (!potBlock.isEmpty()) {
                // Take from pot
                ExtraSounds.MANAGER.blockInteract(potBlock.getContent().asItem(), blockPos);
            } else {
                // Place into pot
                ExtraSounds.MANAGER.blockInteract(this.currentHandStack.getItem(), blockPos);
            }
        }
    }

    public void onInteractEntityAt(ItemStack stackInHand, Entity entity, EntityHitResult hitResult, Vec3d target) {
        final ItemStack currentStack = stackInHand.copy();
        if (entity instanceof ArmorStandEntity armorStandEntity) {
            final EquipmentSlot slotFromPosition = armorStandEntity.getSlotFromPosition(target);
            final EquipmentSlot slotPreferred = this.getPreferredSlot(armorStandEntity, currentStack);
            if (!armorStandEntity.hasStackEquipped(slotFromPosition) && !armorStandEntity.hasStackEquipped(slotPreferred)) {
                return;
            }

            final ItemStack equipped = armorStandEntity.getEquippedStack(slotFromPosition).copy();
            final ItemStack preferred = armorStandEntity.getEquippedStack(slotPreferred).copy();
            if (currentStack.isEmpty() || this.canItemsCombine(currentStack, equipped)) {
                ExtraSounds.MANAGER.blockInteract(equipped.getItem(), BlockPos.ofFloored(hitResult.getPos()));
            } else if (this.canItemsCombine(currentStack, preferred)) {
                ExtraSounds.MANAGER.blockInteract(preferred.getItem(), BlockPos.ofFloored(hitResult.getPos()));
            }
        }
    }
}
