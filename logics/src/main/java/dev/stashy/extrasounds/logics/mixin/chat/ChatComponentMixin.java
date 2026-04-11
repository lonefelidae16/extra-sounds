package dev.stashy.extrasounds.logics.mixin.chat;

import dev.stashy.extrasounds.logics.impl.ChatSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @Shadow
    private int chatScrollbarPos;
    @Shadow
    private @Final Minecraft minecraft;

    @Unique
    private final ChatSoundHandler soundHandler = new ChatSoundHandler();

    @Inject(method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V", at = @At("RETURN"))
    private void extrasounds$receiveMessage(Component message, @Nullable MessageSignature signature, GuiMessageSource messageSource, @Nullable GuiMessageTag indicator, CallbackInfo ci) {
        final LocalPlayer player = this.minecraft.player;
        if (player == null || message == null) {
            return;
        }

        this.soundHandler.onMessage(player, message.getString());
    }

    @Inject(method = "resetChatScroll", at = @At("HEAD"))
    private void extrasounds$resetScroll(CallbackInfo ci) {
        this.soundHandler.resetScroll();
    }

    @Inject(method = "scrollChat", at = @At("RETURN"))
    private void extrasounds$onScroll(int amount, CallbackInfo ci) {
        this.soundHandler.onScroll(this.chatScrollbarPos);
    }
}
