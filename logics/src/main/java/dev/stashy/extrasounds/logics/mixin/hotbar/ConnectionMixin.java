package dev.stashy.extrasounds.logics.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * For Swap with Off-hand action.
 */
@Mixin(Connection.class)
public abstract class ConnectionMixin {
    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @Shadow
    public abstract boolean isConnected();

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lio/netty/channel/ChannelFutureListener;Z)V", at = @At("HEAD"))
    private void extrasounds$hotbarSwapEvent(Packet<?> packet, ChannelFutureListener listener, boolean flush, CallbackInfo ci) {
        final LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !this.isConnected()) {
            return;
        }
        if (!(packet instanceof ServerboundPlayerActionPacket actionC2SPacket)) {
            return;
        }
        if (actionC2SPacket.getAction() != ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }

        this.soundHandler.onSwap(player.getMainHandItem().copy(), player.getOffhandItem().copy());
    }
}
