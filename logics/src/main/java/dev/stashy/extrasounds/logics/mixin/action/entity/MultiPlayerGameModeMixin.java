package dev.stashy.extrasounds.logics.mixin.action.entity;

import dev.stashy.extrasounds.logics.impl.EntitySoundHandler;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Unique
    private final EntitySoundHandler soundHandler = new EntitySoundHandler();

    @Inject(
            method = "interact",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/multiplayer/ClientPacketListener;send(Lnet/minecraft/network/protocol/Packet;)V"
            )
    )
    public void extrasounds$interactEntity(Player player, Entity entity, EntityHitResult hitResult, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player == null || entity == null || player.isSpectator() || entity instanceof Player) {
            return;
        }

        final ItemStack stackInHand = player.getItemInHand(hand);
        if (stackInHand.is(Items.NAME_TAG) && stackInHand.getComponents().has(DataComponents.CUSTOM_NAME)) {
            this.soundHandler.onItemUse(Items.NAME_TAG.getDefaultInstance());
        }
    }
}
