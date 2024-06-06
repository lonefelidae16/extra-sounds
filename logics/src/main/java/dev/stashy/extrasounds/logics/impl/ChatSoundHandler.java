package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public class ChatSoundHandler {
    private int currentLines = 0;

    public void onMessage(PlayerEntity player, String text) {
        boolean containsPlName = false;
        try {
            containsPlName = text.contains("@" + player.getName().getString()) ||
                    text.contains("@" + Objects.requireNonNull(player.getDisplayName()).getString());
        } catch (Exception ignore) {
        }

        if (containsPlName) {
            ExtraSounds.MANAGER.playSound(Sounds.CHAT_MENTION, SoundType.CHAT_MENTION);
        } else {
            ExtraSounds.MANAGER.playSound(Sounds.CHAT, SoundType.CHAT);
        }
    }

    public void onScroll(int line) {
        if (line != this.currentLines) {
            ExtraSounds.MANAGER.playSound(Sounds.INVENTORY_SCROLL, SoundType.CHAT);
            this.currentLines = line;
        }
    }
}
