package dev.stashy.extrasounds.mixin.action;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
public class RepeaterSwitchSound
{
    @Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
    private ActionResult onUse(BlockState instance, World world, PlayerEntity player, Hand hand, BlockHitResult hit) {
        SoundEvent sound = null;
        if (instance.isOf(Blocks.REPEATER) && instance.contains(RepeaterBlock.DELAY)) {
            sound = instance.get(RepeaterBlock.DELAY) == 4 ? Sounds.Actions.REPEATER_RESET : Sounds.Actions.REPEATER_ADD;
        }

        final ActionResult actionResult = instance.onUse(world, player, hand, hit);
        if (actionResult.isAccepted() && sound != null) {
            SoundManager.playSound(sound, SoundType.ACTION, hit.getBlockPos());
        }
        return actionResult;
    }
}
