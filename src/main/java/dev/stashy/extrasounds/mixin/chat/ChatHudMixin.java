package dev.stashy.extrasounds.mixin.chat;

import dev.stashy.extrasounds.SoundManager;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("RETURN"))
    private void messageSound(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null || refresh) {
            return;
        }

        String msg = message.getString();
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (msg.contains("@" + player.getName().getString()) || msg.contains("@" + player.getDisplayName().getString())) {
            SoundManager.playSound(Sounds.CHAT_MENTION, SoundType.CHAT_MENTION);
        } else {
            SoundManager.playSound(Sounds.CHAT, SoundType.CHAT);
        }
    }
}
