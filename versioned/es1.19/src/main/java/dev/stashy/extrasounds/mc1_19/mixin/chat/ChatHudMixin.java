package dev.stashy.extrasounds.mc1_19.mixin.chat;

import dev.stashy.extrasounds.logics.impl.ChatSoundHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @Shadow
    private int scrolledLines;
    @Shadow
    private @Final MinecraftClient client;

    @Unique
    private final ChatSoundHandler soundHandler = new ChatSoundHandler();

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("RETURN"))
    private void extrasounds$receiveMessage(Text message, int messageId, int timestamp, boolean refresh, CallbackInfo ci) {
        final ClientPlayerEntity player = this.client.player;
        if (player == null || message == null) {
            return;
        }

        this.soundHandler.onMessage(player, message.getString());
    }

    @Inject(method = "resetScroll", at = @At("HEAD"))
    private void extrasounds$resetScroll(CallbackInfo ci) {
        this.soundHandler.resetScroll();
    }

    @Inject(method = "scroll", at = @At("RETURN"))
    private void extrasounds$onScroll(int amount, CallbackInfo ci) {
        this.soundHandler.onScroll(this.scrolledLines);
    }
}
