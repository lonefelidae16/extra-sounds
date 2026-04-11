package dev.stashy.extrasounds.logics.mixin.action.block;

import com.llamalad7.mixinextras.sugar.Local;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.AbstractInteractionHandler;
import dev.stashy.extrasounds.logics.impl.state.ActionResultState;
import dev.stashy.extrasounds.logics.mixin.access.ArmorStandAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * For Block Interaction sound.
 */
@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Unique
    private final AbstractInteractionHandler soundHandler = new AbstractInteractionHandler() {
        @Override
        protected EquipmentSlot getPreferredSlot(ArmorStand armorStandEntity, ItemStack itemStack) {
            return armorStandEntity.getEquipmentSlotForItem(itemStack);
        }

        @Override
        protected EquipmentSlot getSlotFromPosition(ArmorStand armorStandEntity, Vec3 position) {
            if (armorStandEntity instanceof ArmorStandAccessor accessor) {
                return accessor.invokeGetClickedSlot(position);
            }
            throw new RuntimeException("Can not cast to ArmorStandAccessor");
        }

        @Override
        protected BlockPos getBlockPos(Vec3 vec3d) {
            return BlockPos.containing(vec3d);
        }

        @Override
        protected boolean isFlowerPotBlocks() {
            return this.blockState.is(BlockTags.FLOWER_POTS);
        }

        @Override
        protected boolean isRedstoneOreBlocks() {
            return this.blockState.is(Blocks.REDSTONE_ORE) || this.blockState.is(Blocks.DEEPSLATE_REDSTONE_ORE);
        }

        @Override
        protected boolean isCampfireBlocks() {
            return this.blockState.is(BlockTags.CAMPFIRES);
        }

        @Override
        protected Optional<?> getCampfireRecipe(CampfireBlockEntity campfireBlockEntity, ItemStack currentHandStack) {
            var world = campfireBlockEntity.getLevel();
            if (world == null) {
                return Optional.empty();
            }

            if (world.recipeAccess().propertySet(RecipePropertySet.CAMPFIRE_INPUT).test(currentHandStack)) {
                return Optional.of(true);
            } else {
                return Optional.empty();
            }
        }

        @Override
        protected boolean shouldSoundArmorStandEquipped(ItemStack currentStack, ItemStack equipped) {
            return currentStack.isEmpty() || ItemStack.isSameItemSameComponents(currentStack, equipped);
        }

        @Override
        protected boolean shouldSoundArmorStandPreferred(ItemStack currentStack, ItemStack preferred) {
            return ItemStack.isSameItemSameComponents(currentStack, preferred);
        }
    };

    @Shadow
    private @Final Minecraft minecraft;

    @Inject(method = "performUseItemOn", at = @At(value = "HEAD"))
    private void extrasounds$storeState(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        final Level world = this.minecraft.level;
        if (player == null || world == null) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();
        this.soundHandler.setInteractionState(
                world.getBlockState(blockPos), world.getBlockEntity(blockPos),
                player.getItemInHand(hand), player.getMainHandItem(), player.getOffhandItem()
        );
    }

    @Inject(
            method = "useItemOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startPrediction(Lnet/minecraft/client/multiplayer/ClientLevel;Lnet/minecraft/client/multiplayer/prediction/PredictiveAction;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$afterOnUse(LocalPlayer player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir, @Local MutableObject<InteractionResult> mutableObject) {
        if (player == null || player.isSpectator()) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();
        final InteractionResult actionResult = mutableObject.get();
        final ActionResultState wrapper;
        if (actionResult == InteractionResult.SUCCESS || actionResult == InteractionResult.SUCCESS_SERVER) {
            wrapper = ActionResultState.SUCCESS;
        } else if (actionResult == InteractionResult.CONSUME) {
            wrapper = ActionResultState.CONSUME;
        } else if (actionResult == InteractionResult.PASS || actionResult == InteractionResult.TRY_WITH_EMPTY_HAND) {
            wrapper = ActionResultState.PASS;
        } else if (actionResult == InteractionResult.FAIL) {
            wrapper = ActionResultState.FAIL;
        } else {
            ExtraSounds.LOGGER.error("Unknown state of ActionResult: {}", actionResult, new RuntimeException());
            return;
        }
        this.soundHandler.onUse(player, blockPos, wrapper);
    }

    @Inject(
            method = "interact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$interactEntityAt(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, @Local Vec3 target) {
        if (player == null || entity == null || hitResult == null || player.isSpectator()) {
            return;
        }

        this.soundHandler.onInteractEntityAt(player.getItemInHand(hand), entity, hitResult, target);
    }
}
