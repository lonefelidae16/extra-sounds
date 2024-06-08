package dev.stashy.extrasounds.mc1_18.mixin.inventory;

import dev.stashy.extrasounds.logics.impl.ScreenScrollHandler;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * For Merchant screen scroll sound.
 */
@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler> {
    @Unique
    private final ScreenScrollHandler soundHandler = new ScreenScrollHandler();

    @Shadow
    int indexStartOffset;

    public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    private void extrasounds$merchantScreenInit(CallbackInfo ci) {
        this.soundHandler.resetScrollPos();
    }

    @Inject(
            method = { "mouseScrolled", "mouseDragged" },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I",
                    shift = At.Shift.AFTER
            )
    )
    private void extrasounds$merchantScreenScroll(CallbackInfoReturnable<Boolean> cir) {
        final int max = this.handler.getRecipes().size() - 7;
        this.soundHandler.onScroll(MathHelper.clamp(this.indexStartOffset, 0, max));
    }
}
