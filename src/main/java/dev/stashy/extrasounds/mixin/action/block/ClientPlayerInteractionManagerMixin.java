package dev.stashy.extrasounds.mixin.action.block;

import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * For Block Interaction sound.
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Unique
    private Block block;
    @Unique
    private BlockState blockState;
    @Unique
    private BlockEntity blockEntity;
    @Unique
    private ItemStack currentHandStack;
    @Unique
    private ItemStack mainHandStack;
    @Unique
    private ItemStack offHandStack;

    @Shadow
    private @Final MinecraftClient client;

    @Unique
    private boolean extrasounds$canInteractBlock(PlayerEntity player) {
        return !player.isSneaking() || (player.isSneaking() && this.mainHandStack.isEmpty() && this.offHandStack.isEmpty());
    }

    @Inject(method = "interactBlockInternal", at = @At(value = "HEAD"))
    private void extrasounds$storeState(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        final World world = this.client.world;
        if (world == null) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();
        this.blockState = world.getBlockState(blockPos);
        this.blockEntity = world.getBlockEntity(blockPos);
        this.block = this.blockState.getBlock();
        this.currentHandStack = player.getStackInHand(hand).copy();
        this.mainHandStack = player.getMainHandStack().copy();
        this.offHandStack = player.getOffHandStack().copy();
    }

    @Inject(
            method = "interactBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void extrasounds$afterOnUse(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir, MutableObject<ActionResult> mutableObject) {
        final World world = this.client.world;
        if (world == null || player.isSpectator()) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();

        if (this.blockState.isOf(Blocks.REPEATER) &&
                this.blockState.contains(RepeaterBlock.DELAY) &&
                this.extrasounds$canInteractBlock(player)
        ) {
            // Repeater
            final SoundEvent sound = this.blockState.get(RepeaterBlock.DELAY) == 4 ? Sounds.Actions.REPEATER_RESET : Sounds.Actions.REPEATER_ADD;
            ExtraSounds.MANAGER.blockInteract(sound, blockPos);
        } else if (this.blockState.isOf(Blocks.DAYLIGHT_DETECTOR) &&
                this.blockState.contains(DaylightDetectorBlock.INVERTED) &&
                this.extrasounds$canInteractBlock(player)
        ) {
            // Daylight Detector
            final SoundEvent sound = this.blockState.get(DaylightDetectorBlock.INVERTED) ? Sounds.Actions.REDSTONE_COMPONENT_ON : Sounds.Actions.REDSTONE_COMPONENT_OFF;
            ExtraSounds.MANAGER.blockInteract(sound, blockPos);
        } else if (this.blockState.isOf(Blocks.REDSTONE_WIRE) && this.extrasounds$canInteractBlock(player)) {
            // Redstone Wire
            ExtraSounds.MANAGER.blockInteract(Sounds.Actions.REDSTONE_WIRE_CHANGE, blockPos);
        } else if (this.blockState.isIn(BlockTags.REDSTONE_ORES) &&
                this.blockState.contains(RedstoneOreBlock.LIT) &&
                this.extrasounds$canInteractBlock(player) && !(this.mainHandStack.getItem() instanceof BlockItem)
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

    @Inject(
            method = "interactEntityAtLocation",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
                    shift = At.Shift.AFTER
            ), locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void extrasounds$interactEntityAt(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir, Vec3d target) {
        if (player == null || hitResult == null || player.isSpectator()) {
            return;
        }

        final ItemStack currentStack = player.getStackInHand(hand).copy();
        if (entity instanceof ArmorStandEntity armorStandEntity) {
            final EquipmentSlot slotFromPosition = armorStandEntity.getSlotFromPosition(target);
            final EquipmentSlot slotPreferred = MobEntity.getPreferredEquipmentSlot(currentStack);
            if (!armorStandEntity.hasStackEquipped(slotFromPosition) && !armorStandEntity.hasStackEquipped(slotPreferred)) {
                return;
            }

            final ItemStack equipped = armorStandEntity.getEquippedStack(slotFromPosition).copy();
            final ItemStack preferred = armorStandEntity.getEquippedStack(slotPreferred).copy();
            if (currentStack.isEmpty() || ItemStack.areItemsAndComponentsEqual(currentStack, equipped)) {
                ExtraSounds.MANAGER.blockInteract(equipped.getItem(), BlockPos.ofFloored(hitResult.getPos()));
            } else if (ItemStack.areItemsAndComponentsEqual(currentStack, preferred)) {
                ExtraSounds.MANAGER.blockInteract(preferred.getItem(), BlockPos.ofFloored(hitResult.getPos()));
            }
        }
    }
}
