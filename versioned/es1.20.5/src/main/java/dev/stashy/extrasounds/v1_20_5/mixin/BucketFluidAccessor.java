package dev.stashy.extrasounds.v1_20_5.mixin;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BucketItem.class)
public interface BucketFluidAccessor {
    @Accessor
    Fluid getFluid();
}
