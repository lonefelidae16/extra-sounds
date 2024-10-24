package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.state.ActionResultState;
import dev.stashy.extrasounds.sounds.Sounds;
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
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

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

    protected abstract Optional<?> getCampfireRecipe(CampfireBlockEntity campfireBlockEntity, ItemStack currentHandStack);

    protected abstract boolean shouldSoundArmorStandEquipped(ItemStack currentStack, ItemStack equipped);

    protected abstract boolean shouldSoundArmorStandPreferred(ItemStack currentStack, ItemStack preferred);

    private boolean canInteractBlock(PlayerEntity player) {
        return !player.isSneaking() || (player.isSneaking() && this.mainHandStack.isEmpty() && this.offHandStack.isEmpty());
    }

    public final void setInteractionState(BlockState blockState, BlockEntity blockEntity, ItemStack stackInHand, ItemStack mainHandStack, ItemStack offHandStack) {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
        this.block = blockState.getBlock();
        this.currentHandStack = stackInHand.copy();
        this.mainHandStack = mainHandStack.copy();
        this.offHandStack = offHandStack.copy();
    }

    public final void onUse(ClientPlayerEntity player, BlockPos blockPos, ActionResultState actionResult) {
        final boolean bCanInteract = this.canInteractBlock(player);

        if (this.blockState.isOf(Blocks.REPEATER) &&
                this.blockState.contains(RepeaterBlock.DELAY) &&
                bCanInteract
        ) {
            // Repeater
            final var sound = this.blockState.get(RepeaterBlock.DELAY) == 4 ? Sounds.Actions.REPEATER_RESET : Sounds.Actions.REPEATER_ADD;
            ExtraSounds.MANAGER.blockInteract(sound, blockPos);
        } else if (this.blockState.isOf(Blocks.DAYLIGHT_DETECTOR) &&
                this.blockState.contains(DaylightDetectorBlock.INVERTED) &&
                bCanInteract
        ) {
            // Daylight Detector
            final var sound = this.blockState.get(DaylightDetectorBlock.INVERTED) ? Sounds.Actions.REDSTONE_COMPONENT_ON : Sounds.Actions.REDSTONE_COMPONENT_OFF;
            ExtraSounds.MANAGER.blockInteract(sound, blockPos);
        } else if (this.blockState.isOf(Blocks.REDSTONE_WIRE) && bCanInteract &&
                actionResult == ActionResultState.SUCCESS
        ) {
            // Redstone Wire
            ExtraSounds.MANAGER.blockInteract(Sounds.Actions.REDSTONE_WIRE_CHANGE, blockPos);
        } else if (this.isRedstoneOreBlocks() &&
                this.blockState.contains(RedstoneOreBlock.LIT) &&
                bCanInteract && !(this.mainHandStack.getItem() instanceof BlockItem)
        ) {
            // Redstone Ores
            ExtraSounds.MANAGER.blockInteract(this.block.asItem(), blockPos);
        } else if (this.isCampfireBlocks() && (this.blockEntity instanceof CampfireBlockEntity campfireBlockEntity)) {
            // Put item on Campfire
            var recipe = this.getCampfireRecipe(campfireBlockEntity, this.currentHandStack);
            if (recipe.isPresent() && actionResult == ActionResultState.CONSUME) {
                ExtraSounds.MANAGER.blockInteract(this.currentHandStack.getItem(), blockPos);
            }
        } else if (this.isFlowerPotBlocks() &&
                (this.block instanceof FlowerPotBlock potBlock) &&
                actionResult == ActionResultState.SUCCESS
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

    public final void onInteractEntityAt(ItemStack stackInHand, Entity entity, EntityHitResult hitResult, Vec3d target) {
        final ItemStack currentStack = stackInHand.copy();
        if (entity instanceof ArmorStandEntity armorStandEntity) {
            final EquipmentSlot slotFromPosition = this.getSlotFromPosition(armorStandEntity, target);
            final EquipmentSlot slotPreferred = this.getPreferredSlot(armorStandEntity, currentStack);
            if (!armorStandEntity.hasStackEquipped(slotFromPosition) && !armorStandEntity.hasStackEquipped(slotPreferred)) {
                return;
            }

            final ItemStack equipped = armorStandEntity.getEquippedStack(slotFromPosition).copy();
            final ItemStack preferred = armorStandEntity.getEquippedStack(slotPreferred).copy();
            if (this.shouldSoundArmorStandEquipped(currentStack, equipped)) {
                ExtraSounds.MANAGER.blockInteract(equipped.getItem(), this.getBlockPos(hitResult.getPos()));
            } else if (this.shouldSoundArmorStandPreferred(currentStack, preferred)) {
                ExtraSounds.MANAGER.blockInteract(preferred.getItem(), this.getBlockPos(hitResult.getPos()));
            }
        }
    }
}
