package dev.stashy.extrasounds.mixin.hotbar;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Swap with Off-hand action.
 */
@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow
    public abstract boolean isOpen();

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;Z)V", at = @At("HEAD"))
    private void extrasounds$hotbarSwapEvent(Packet<?> packet, PacketCallbacks callbacks, boolean flush, CallbackInfo ci) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) {
            return;
        }
        if (!this.isOpen()) {
            return;
        }
        if (!(packet instanceof PlayerActionC2SPacket actionC2SPacket)) {
            return;
        }
        if (actionC2SPacket.getAction() != PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }

        final ItemStack offHandStack = player.getOffHandStack();
        final ItemStack mainHandStack = player.getMainHandStack();
        if (!offHandStack.isEmpty()) {
            SoundManager.playSound(offHandStack, SoundType.PICKUP);
        } else if (!mainHandStack.isEmpty()) {
            SoundManager.playSound(mainHandStack, SoundType.PICKUP);
        }
    }
}
