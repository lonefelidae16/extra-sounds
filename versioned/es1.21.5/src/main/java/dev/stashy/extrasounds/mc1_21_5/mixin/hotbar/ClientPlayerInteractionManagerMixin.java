package dev.stashy.extrasounds.mc1_21_5.mixin.hotbar;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.impl.VersionedHotbarSoundHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
    @Shadow
    private @Final MinecraftClient client;

    @Unique
    private final VersionedHotbarSoundHandler soundHandler = ExtraSounds.MANAGER.getHotbarSoundHandler();

    @Inject(method = "pickItemFromBlock", at = @At("HEAD"))
    private void extrasounds$storePickingBlock(BlockPos blockPos, boolean includeData, CallbackInfo ci) {
        final ClientWorld world = this.client.world;
        final ClientPlayerEntity player = this.client.player;
        if (world == null || player == null) {
            return;
        }

        if (!player.canInteractWithBlockAt(blockPos, 1.0)) {
            return;
        }

        final boolean bCreative = player.isInCreativeMode() && includeData;

        this.extrasounds$storePickingItem(world.getBlockState(blockPos).getPickStack(world, blockPos, bCreative).getItem());
    }

    @Inject(method = "pickItemFromEntity", at = @At("HEAD"))
    private void extrasounds$storePickingEntity(Entity entity, boolean includeData, CallbackInfo ci) {
        final ClientPlayerEntity player = this.client.player;
        if (entity == null || player == null) {
            return;
        }

        if (!player.canInteractWithEntity(entity, 3.0)) {
            return;
        }

        final ItemStack pickBlockStack = entity.getPickBlockStack();
        if (pickBlockStack == null) {
            return;
        }

        this.extrasounds$storePickingItem(pickBlockStack.getItem());
    }

    @Unique
    private void extrasounds$storePickingItem(Item item) {
        final ClientPlayerEntity player = this.client.player;
        if (player == null || item == Items.AIR) {
            return;
        }

        if (player.isCreative() || player.getInventory().contains(itemStack -> itemStack.getItem() == item)) {
            this.soundHandler.storePickingItem(item);
        }
    }
}
