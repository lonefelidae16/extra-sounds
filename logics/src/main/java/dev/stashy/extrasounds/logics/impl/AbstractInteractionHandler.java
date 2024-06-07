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
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class AbstractInteractionHandler {
    protected BlockState blockState;
    protected BlockEntity blockEntity;
    protected Block block;
    protected ItemStack currentHandStack;
    protected ItemStack mainHandStack;
    protected ItemStack offHandStack;

    protected abstract EquipmentSlot getPreferredSlot(ArmorStandEntity armorStandEntity, ItemStack itemStack);

    protected abstract EquipmentSlot getSlotFromPosition(ArmorStandEntity armorStandEntity, Vec3d position);

    protected abstract BlockPos getBlockPos(Vec3d vec3d);

    protected abstract boolean isFlowerPotBlocks();

    protected abstract boolean isRedstoneOreBlocks();

    protected abstract boolean isCampfireBlocks();

    protected abstract boolean canSoundArmorStandEquipped(ItemStack currentStack, ItemStack equipped);

    protected abstract boolean canSoundArmorStandPreferred(ItemStack currentStack, ItemStack preferred);

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

    public final void onUse(ClientPlayerEntity player, BlockPos blockPos, ActionResult actionResult) {
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
        } else if (this.blockState.isOf(Blocks.REDSTONE_WIRE) && this.canInteractBlock(player) &&
                actionResult == ActionResult.SUCCESS
        ) {
            // Redstone Wire
            ExtraSounds.MANAGER.blockInteract(Sounds.Actions.REDSTONE_WIRE_CHANGE, blockPos);
        } else if (this.isRedstoneOreBlocks() &&
                this.blockState.contains(RedstoneOreBlock.LIT) &&
                this.canInteractBlock(player) && !(this.mainHandStack.getItem() instanceof BlockItem)
        ) {
            // Redstone Ores
            ExtraSounds.MANAGER.blockInteract(this.block.asItem(), blockPos);
        } else if (this.isCampfireBlocks() && (this.blockEntity instanceof CampfireBlockEntity campfireBlockEntity)) {
            // Put item on Campfire
            var recipe = campfireBlockEntity.getRecipeFor(this.currentHandStack);
            if (recipe.isPresent() && actionResult == ActionResult.CONSUME) {
                ExtraSounds.MANAGER.blockInteract(this.currentHandStack.getItem(), blockPos);
            }
        } else if (this.isFlowerPotBlocks() &&
                (this.block instanceof FlowerPotBlock potBlock) &&
                actionResult == ActionResult.SUCCESS
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
            final EquipmentSlot slotFromPosition = this.getSlotFromPosition(armorStandEntity, target);
            final EquipmentSlot slotPreferred = this.getPreferredSlot(armorStandEntity, currentStack);
            if (!armorStandEntity.hasStackEquipped(slotFromPosition) && !armorStandEntity.hasStackEquipped(slotPreferred)) {
                return;
            }

            final ItemStack equipped = armorStandEntity.getEquippedStack(slotFromPosition).copy();
            final ItemStack preferred = armorStandEntity.getEquippedStack(slotPreferred).copy();
            if (this.canSoundArmorStandEquipped(currentStack, equipped)) {
                ExtraSounds.MANAGER.blockInteract(equipped.getItem(), this.getBlockPos(hitResult.getPos()));
            } else if (this.canSoundArmorStandPreferred(currentStack, preferred)) {
                ExtraSounds.MANAGER.blockInteract(preferred.getItem(), this.getBlockPos(hitResult.getPos()));
            }
        }
    }
}
