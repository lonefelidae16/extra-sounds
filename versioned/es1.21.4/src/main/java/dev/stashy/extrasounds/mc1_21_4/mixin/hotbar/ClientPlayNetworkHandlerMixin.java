package dev.stashy.extrasounds.mc1_21_4.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.TickablePacketListener;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin extends ClientCommonNetworkHandler implements ClientPlayPacketListener, TickablePacketListener {
    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    protected ClientPlayNetworkHandlerMixin(MinecraftClient client, ClientConnection connection, ClientConnectionState connectionState) {
        super(client, connection, connectionState);
    }

    @Inject(
            method = "onUpdateSelectedSlot",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", opcode = Opcodes.PUTFIELD)
    )
    private void extrasounds$itemPickSound(CallbackInfo ci) {
        ClientPlayerEntity player = this.client.player;
        if (player == null) {
            return;
        }
        this.soundHandler.onItemPick();
    }
}
