package dev.stashy.extrasounds.impl;

import dev.stashy.extrasounds.SoundManager;

public class TextFieldContainer {
    public int cursorStart = 0;
    public int cursorEnd = 0;

    public boolean canErase(int position, int length, int selectionStart, int selectionEnd) {
        final boolean bHeadBackspace = position < 0 && selectionStart <= 0;
        final boolean bTailDelete = position > 0 && selectionEnd >= length;
        return !((bHeadBackspace || bTailDelete) && selectionStart == selectionEnd);
    }

    public void onCursorChanged(int selectionStart, int selectionEnd) {
        final boolean bSamePos = this.cursorStart == selectionStart && this.cursorEnd == selectionEnd;
        if (bSamePos) {
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
