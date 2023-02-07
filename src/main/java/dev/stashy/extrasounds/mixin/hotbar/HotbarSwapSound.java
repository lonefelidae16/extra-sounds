package dev.stashy.extrasounds.mixin.hotbar;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class HotbarSwapSound {
    @Shadow
    private @Final MinecraftClient client;

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void extrasounds$hotbarSwapEvent(Packet<?> packet, CallbackInfo ci) {
        if (this.client.player == null) {
            return;
        }


        if (!(packet instanceof PlayerActionC2SPacket actionC2SPacket)) {
            return;
        }

        if (actionC2SPacket.getAction() != PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND) {
            return;
        }

        ItemStack itemStack = this.client.player.getOffHandStack();
        if (itemStack.isEmpty()) {
            itemStack = this.client.player.getMainHandStack();
        }
        SoundManager.playSound(itemStack, SoundType.PICKUP);
    }
}
