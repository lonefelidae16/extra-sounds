package dev.stashy.extrasounds.logics.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class MultiPlayerGameModeMixin {
    @Shadow
    private @Final Minecraft minecraft;

    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @Inject(method = "handlePickItemFromBlock", at = @At("HEAD"))
    private void extrasounds$storePickingBlock(BlockPos blockPos, boolean includeData, CallbackInfo ci) {
        final ClientLevel world = this.minecraft.level;
        final LocalPlayer player = this.minecraft.player;
        if (world == null || player == null) {
            return;
        }

        if (!player.isWithinBlockInteractionRange(blockPos, 1.0)) {
            return;
        }

        final boolean bCreative = player.hasInfiniteMaterials() && includeData;

        this.extrasounds$storePickingItem(world.getBlockState(blockPos).getCloneItemStack(world, blockPos, bCreative).copy());
    }

    @Inject(method = "handlePickItemFromEntity", at = @At("HEAD"))
    private void extrasounds$storePickingEntity(Entity entity, boolean includeData, CallbackInfo ci) {
        final LocalPlayer player = this.minecraft.player;
        if (entity == null || player == null) {
            return;
        }

        if (!player.isWithinEntityInteractionRange(entity, 3.0)) {
            return;
        }

        final ItemStack pickBlockStack = entity.getPickResult();
        if (pickBlockStack == null) {
            return;
        }

        this.extrasounds$storePickingItem(pickBlockStack.copy());
    }

    @Unique
    private void extrasounds$storePickingItem(ItemStack target) {
        final LocalPlayer player = this.minecraft.player;
        if (player == null || target.getItem() == Items.AIR) {
            return;
        }

        if (player.isCreative() || player.getInventory().contains(stack -> ExtraSounds.MAIN.canItemsCombine(stack, target)) && !ExtraSounds.MAIN.canItemsCombine(player.getOffhandItem(), target)) {
            this.soundHandler.storePickingItem(target.getItem());
        }
    }
}
