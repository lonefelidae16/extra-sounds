package dev.stashy.extrasounds.impl;

import dev.stashy.extrasounds.SoundManager;

/**
 * Helper class for managing {@link net.minecraft.client.gui.widget.TextFieldWidget} and its inherited class.
 */
public class TextFieldState {
    /**
     * Position of the start.
     */
    public int cursorStart = 0;
    /**
     * Position of the end.
     */
    public int cursorEnd = 0;

    /**
     * Triggers the erase action.
     *
     * @param offset         -1 or +1
     * @param length         The text length.
     * @param selectionStart Current position of the start of the selection.
     * @param selectionEnd   Current position of the end of the selection.
     */
    public void onErase(int offset, int length, int selectionStart, int selectionEnd) {
        final boolean bHeadBackspace = offset < 0 && selectionStart <= 0;
        final boolean bTailDelete = offset > 0 && selectionEnd >= length;
        if ((bHeadBackspace || bTailDelete) && selectionStart == selectionEnd) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.ERASE);
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
        SoundManager.keyboard(SoundManager.KeyType.CURSOR);
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
}
