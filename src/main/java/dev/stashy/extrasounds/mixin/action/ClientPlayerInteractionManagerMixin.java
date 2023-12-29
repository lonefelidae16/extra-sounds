package dev.stashy.extrasounds.mixin.action;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CampfireBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Shadow
    private @Final MinecraftClient client;

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
    }

    @Inject(method = "interactBlockInternal", at = @At(value = "RETURN", ordinal = 2))
    private void extrasounds$afterOnUse(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        final World world = this.client.world;
        if (world == null) {
            return;
        }

        final BlockPos blockPos = hitResult.getBlockPos();

        if (this.blockState.isOf(Blocks.REPEATER) && this.blockState.contains(RepeaterBlock.DELAY)) {
            // Repeater
            final SoundEvent sound = this.blockState.get(RepeaterBlock.DELAY) == 4 ? Sounds.Actions.REPEATER_RESET : Sounds.Actions.REPEATER_ADD;
            SoundManager.blockInteract(sound, blockPos);
        } else if (this.blockState.isIn(BlockTags.CAMPFIRES) && (this.blockEntity instanceof CampfireBlockEntity campfireBlockEntity)) {
            // Put item on Campfire
            var recipe = campfireBlockEntity.getRecipeFor(this.currentHandStack);
            if (recipe.isPresent()) {
                SoundManager.blockInteract(this.currentHandStack, blockPos);
            }
        } else if (this.blockState.isIn(BlockTags.FLOWER_POTS) &&
                (this.block instanceof FlowerPotBlock potBlock) &&
                cir.getReturnValue() == ActionResult.SUCCESS
        ) {
            if (!potBlock.isEmpty()) {
                // Take from pot
                SoundManager.blockInteract(potBlock.getContent().asItem().getDefaultStack(), blockPos);
            } else {
                // Place into pot
                SoundManager.blockInteract(this.currentHandStack, blockPos);
            }
        }
    }
}
