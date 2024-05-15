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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Shadow
    private int scrolledLines;
    @Shadow
    private @Final MinecraftClient client;

    @Unique
    private int currentLines;

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", at = @At("RETURN"))
    private void extrasounds$receiveMessage(Text message, @Nullable MessageSignatureData signature, int ticks, @Nullable MessageIndicator indicator, boolean refresh, CallbackInfo ci) {
        final ClientPlayerEntity player = this.client.player;
        if (player == null || message == null || refresh) {
            return;
        }

        String msg = message.getString();
        boolean containsPlName = false;
        try {
            containsPlName = msg.contains("@" + player.getName().getString()) ||
                    msg.contains("@" + Objects.requireNonNull(player.getDisplayName()).getString());
        } catch (Exception ignore) {
        }

        if (containsPlName) {
            SoundManager.playSound(Sounds.CHAT_MENTION, SoundType.CHAT_MENTION);
        } else {
            SoundManager.playSound(Sounds.CHAT, SoundType.CHAT);
        }
    }

    @Inject(method = "scroll", at = @At("RETURN"))
    private void extrasounds$onScroll(int amount, CallbackInfo ci) {
        if (this.scrolledLines != this.currentLines) {
            SoundManager.playSound(Sounds.INVENTORY_SCROLL, SoundType.CHAT);
            this.currentLines = this.scrolledLines;
        }
    }
}
