package dev.stashy.extrasounds.mc1_20.mixin.chat;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.ChatSoundHandler;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
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
    private final ChatSoundHandler soundHandler = new ChatSoundHandler();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("RETURN"))
    private void extrasounds$receiveMessage(Text message, @Nullable MessageSignatureData signature, @Nullable MessageIndicator indicator, CallbackInfo ci) {
        final ClientPlayerEntity player = this.client.player;
        if (player == null || message == null) {
            return;
        }

        this.soundHandler.onMessage(player, message.getString());
    }

    @Inject(method = "scroll", at = @At("RETURN"))
    private void extrasounds$onScroll(int amount, CallbackInfo ci) {
        this.soundHandler.onScroll(this.scrolledLines);
    }
}
