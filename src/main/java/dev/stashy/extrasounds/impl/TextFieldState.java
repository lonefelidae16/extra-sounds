package dev.stashy.extrasounds.impl;

import dev.stashy.extrasounds.SoundManager;

public class TextFieldState {
    public int cursorStart = 0;
    public int cursorEnd = 0;

    public void onErase(int offset, int length, int selectionStart, int selectionEnd) {
        final boolean bHeadBackspace = offset < 0 && selectionStart <= 0;
        final boolean bTailDelete = offset > 0 && selectionEnd >= length;
        if ((bHeadBackspace || bTailDelete) && selectionStart == selectionEnd) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.ERASE);
    }

    public void onCursorChanged(int selectionStart, int selectionEnd) {
        if (!isPosUpdated(selectionStart, selectionEnd)) {
            return;
        }
        SoundManager.keyboard(SoundManager.KeyType.CURSOR);
        this.cursorStart = selectionStart;
        this.cursorEnd = selectionEnd;
    }

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
