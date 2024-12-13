package dev.stashy.extrasounds.mc1_21_4.mixin.action.entity;

import dev.stashy.extrasounds.logics.impl.EntitySoundHandler;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Unique
    private final EntitySoundHandler soundHandler = new EntitySoundHandler();

    @Inject(
            method = "interactEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
            )
    )
    public void extrasounds$interactEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player == null || entity == null || player.isSpectator() || entity instanceof PlayerEntity) {
            return;
        }

        final ItemStack stackInHand = player.getStackInHand(hand);
        if (stackInHand.isOf(Items.NAME_TAG) && stackInHand.getComponents().contains(DataComponentTypes.CUSTOM_NAME)) {
            this.soundHandler.onItemUse(Items.NAME_TAG);
        }
    }
}
