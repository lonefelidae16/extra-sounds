package dev.stashy.extrasounds.logics.mixin.typing;

import dev.stashy.extrasounds.logics.impl.TextFieldHandler;
import net.minecraft.client.gui.components.CommandSuggestions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandSuggestions.SuggestionsList.class)
public abstract class SuggestionsListMixin {
    @Shadow
    private int current;
    @Shadow
    private boolean tabCycles;

    @Unique
    private final TextFieldHandler soundHandler = new TextFieldHandler();
    @Unique
    private int currentPos;

    @Inject(method = "select", at = @At("RETURN"))
    private void extrasounds$suggestionSelect(CallbackInfo ci) {
        if (this.current != this.currentPos) {
            this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
            this.currentPos = this.current;
        }
    }

    @Inject(method = "useSuggestion", at = @At("HEAD"))
    private void extrasounds$suggestionComplete(CallbackInfo ci) {
        if (this.tabCycles) {
            return;
        }
        this.soundHandler.onKey(TextFieldHandler.KeyType.INSERT);
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/CommandSuggestions;hide()V"))
    private void extrasounds$closeWindow(CallbackInfoReturnable<Boolean> cir) {
        this.soundHandler.onKey(TextFieldHandler.KeyType.CURSOR);
    }
}
