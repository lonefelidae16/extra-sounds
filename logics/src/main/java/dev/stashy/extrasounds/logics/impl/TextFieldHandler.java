package dev.stashy.extrasounds.logics.impl;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;

/**
 * Helper class for managing {@link net.minecraft.client.gui.widget.TextFieldWidget} and its inherited class.
 */
public class TextFieldHandler {
    public enum KeyType {
        ERASE,
        CUT,
        INSERT,
        PASTE,
        RETURN,
        CURSOR
    }

    /**
     * Text position of the start.
     */
    private int cursorStart = 0;
    /**
     * Text position of the end.
     */
    private int cursorEnd = 0;

    /**
     * Triggers the erase action.
     *
     * @param offset         -1 or +1
     * @param length         A text length.
     * @param selectionStart Current position of the start of the selection.
     * @param selectionEnd   Current position of the end of the selection.
     */
    public void onCharErase(int offset, int length, int selectionStart, int selectionEnd) {
        final boolean bHeadBackspace = offset < 0 && selectionStart <= 0;
        final boolean bTailDelete = offset > 0 && selectionEnd >= length;
        if ((bHeadBackspace || bTailDelete) && selectionStart == selectionEnd) {
            return;
        }
        this.onKey(KeyType.ERASE);
    }

    /**
     * Triggers the cursor move action.
     *
     * @param selectionStart Current position of the start of the selection.
     * @param selectionEnd   Current position of the end of the selection.
     */
    public void onCursorChanged(int selectionStart, int selectionEnd) {
        if (!isPosUpdated(selectionStart, selectionEnd)) {
            return;
        }
        this.onKey(KeyType.CURSOR);
        this.cursorStart = selectionStart;
        this.cursorEnd = selectionEnd;
    }

    /**
     * Checks if cursor position has moved.
     *
     * @param selectionStart Current position of the start of the selection.
     * @param selectionEnd   Current position of the end of the selection.
     * @return <code>true</code> if the movement of the cursor position is detected.
     */
    public boolean isPosUpdated(int selectionStart, int selectionEnd) {
        return this.cursorStart != selectionStart || this.cursorEnd != selectionEnd;
    }

    public void setCursor(int pos) {
        this.cursorStart = this.cursorEnd = pos;
    }

    public void setCursorStart(int cursorStart) {
        this.cursorStart = cursorStart;
    }

    public void setCursorEnd(int cursorEnd) {
        this.cursorEnd = cursorEnd;
    }

    public void onKey(KeyType type) {
        if (type == null) {
            ExtraSounds.LOGGER.error("Null argument of type '{}' was passed!",
                    KeyType.class.getSimpleName(),
                    new IllegalArgumentException("'type' must be non-null.")
            );
            return;
        }

        switch (type) {
            case ERASE -> ExtraSounds.MANAGER.playSound(Sounds.KEYBOARD_ERASE, SoundType.TYPING);
            case CUT -> ExtraSounds.MANAGER.playSound(Sounds.KEYBOARD_CUT, SoundType.TYPING);
            case CURSOR, RETURN -> ExtraSounds.MANAGER.playSound(Sounds.KEYBOARD_MOVE, SoundType.TYPING);
            case INSERT -> ExtraSounds.MANAGER.playSound(Sounds.KEYBOARD_TYPE, SoundType.TYPING);
            case PASTE -> ExtraSounds.MANAGER.playSound(Sounds.KEYBOARD_PASTE, SoundType.TYPING);
        }
    }
}
