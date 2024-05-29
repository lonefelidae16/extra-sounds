package dev.stashy.extrasounds.impl;

import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.Mixers;
import dev.stashy.extrasounds.sounds.Sounds;

/**
 * Helper class for managing {@link net.minecraft.client.gui.screen.Screen} and its inherited class.
 */
public class ScreenScrollHandler {
    /**
     * Latest scroll time in milliseconds.
     */
    private long lastScrollTime;
    /**
     * Current scroll position.
     */
    private int lastScrollPos;

    public ScreenScrollHandler() {
        this.resetScrollPos();
    }

    /**
     * Resets scroll states.
     */
    public void resetScrollPos() {
        this.lastScrollTime = 0;
        this.lastScrollPos = 0;
    }

    /**
     * Triggers the screen scroll action.
     *
     * @param row Target screen Y offset.
     */
    public void onScroll(int row) {
        final long now = System.currentTimeMillis();
        final long timeDiff = now - this.lastScrollTime;
        if (timeDiff > 20 && this.lastScrollPos != row) {
            ExtraSounds.MANAGER.playSound(
                    Sounds.INVENTORY_SCROLL,
                    (1f - 0.1f + 0.1f * Math.min(1, 50f / timeDiff)),
                    Mixers.INVENTORY);
            this.lastScrollTime = now;
            this.lastScrollPos = row;
        }
    }
}
