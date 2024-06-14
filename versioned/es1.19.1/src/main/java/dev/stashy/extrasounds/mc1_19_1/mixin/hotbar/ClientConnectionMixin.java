package dev.stashy.extrasounds.mc1_19_1.mixin.hotbar;

import dev.stashy.extrasounds.logics.impl.HotbarSoundHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketCallbacks;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Swap with Off-hand action.
 */
@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Unique
    private final HotbarSoundHandler soundHandler = new HotbarSoundHandler();

    @Shadow
    public abstract boolean isOpen();

    @Inject(method = "send(Lnet/minecraft/network/Packet;Lnet/minecraft/network/PacketCallbacks;)V", at = @At("HEAD"))
    private void extrasounds$hotbarSwapEvent(Packet<?> packet, PacketCallbacks callbacks, CallbackInfo ci) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || !this.isOpen()) {
            return;
        }
        if (!(packet instanceof PlayerActionC2SPacket actionC2SPacket)) {
            return;
        }
        if (actionC2SPacket.getAction() != PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }

        this.soundHandler.onSwapEvent(player.getMainHandStack().getItem(), player.getOffHandStack().getItem());
    }
}
