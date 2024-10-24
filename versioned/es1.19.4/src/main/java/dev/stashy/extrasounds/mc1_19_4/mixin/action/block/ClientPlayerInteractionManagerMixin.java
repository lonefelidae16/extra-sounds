package dev.stashy.extrasounds.mc1_19_4.mixin.action.block;

import dev.stashy.extrasounds.logics.impl.AbstractInteractionHandler;
import dev.stashy.extrasounds.logics.impl.state.ActionResultState;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
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

import java.util.Optional;

/**
 * For Block Interaction sound.
 */
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Unique
    private final AbstractInteractionHandler soundHandler = new AbstractInteractionHandler() {
        @Override
        protected EquipmentSlot getPreferredSlot(ArmorStandEntity armorStandEntity, ItemStack itemStack) {
            return MobEntity.getPreferredEquipmentSlot(itemStack);
        }

        @Override
        protected EquipmentSlot getSlotFromPosition(ArmorStandEntity armorStandEntity, Vec3d position) {
            return armorStandEntity.getSlotFromPosition(position);
        }

        @Override
        protected BlockPos getBlockPos(Vec3d vec3d) {
            return BlockPos.ofFloored(vec3d);
        }

        @Override
        protected boolean isFlowerPotBlocks() {
            return this.blockState.isIn(BlockTags.FLOWER_POTS);
        }

        @Override
        protected boolean isRedstoneOreBlocks() {
            return this.blockState.isIn(BlockTags.REDSTONE_ORES);
        }

        @Override
        protected boolean isCampfireBlocks() {
            return this.blockState.isIn(BlockTags.CAMPFIRES);
        }

        @Override
        protected Optional<?> getCampfireRecipe(CampfireBlockEntity campfireBlockEntity, ItemStack currentHandStack) {
            return campfireBlockEntity.getRecipeFor(currentHandStack);
        }

        @Override
        protected boolean shouldSoundArmorStandEquipped(ItemStack currentStack, ItemStack equipped) {
            return currentStack.isEmpty() || ItemStack.canCombine(currentStack, equipped);
        }

        @Override
        protected boolean shouldSoundArmorStandPreferred(ItemStack currentStack, ItemStack preferred) {
            return ItemStack.canCombine(currentStack, preferred);
        }
    };

    @Shadow
    private @Final MinecraftClient client;

    @Inject(method = "interactBlockInternal", at = @At(value = "HEAD"))
    private void extrasounds$storeState(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        final World world = this.client.world;
        if (player == null || world == null) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();
        this.soundHandler.setInteractionState(world.getBlockState(blockPos), world.getBlockEntity(blockPos),
                player.getStackInHand(hand), player.getMainHandStack(), player.getOffHandStack());
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
        if (player == null || player.isSpectator()) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();
        final ActionResultState wrapper = switch (mutableObject.getValue()) {
            case SUCCESS -> ActionResultState.SUCCESS;
            case CONSUME, CONSUME_PARTIAL -> ActionResultState.CONSUME;
            case PASS -> ActionResultState.PASS;
            case FAIL -> ActionResultState.FAIL;
        };
        this.soundHandler.onUse(player, blockPos, wrapper);
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
        if (player == null || entity == null || hitResult == null || player.isSpectator()) {
            return;
        }

        this.soundHandler.onInteractEntityAt(player.getStackInHand(hand), entity, hitResult, target);
    }
}
