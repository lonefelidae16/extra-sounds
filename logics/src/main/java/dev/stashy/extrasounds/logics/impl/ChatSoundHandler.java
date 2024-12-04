package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.minecraft.entity.player.PlayerEntity;

import java.util.Objects;

public final class ChatSoundHandler {
    private int currentLines = 0;

    public void onMessage(PlayerEntity player, String text) {
        boolean containsPlName = false;
        try {
            containsPlName |= text.contains("@" + player.getName().getString());
            containsPlName |= text.contains("@" + Objects.requireNonNull(player.getDisplayName()).getString());
        } catch (Exception ignore) {
        }

        if (containsPlName && !ExtraSounds.MANAGER.isMuted(SoundType.CHAT_MENTION)) {
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

    public void resetScroll() {
        this.currentLines = 0;
    }
}
