package dev.stashy.extrasounds.logics.mixin.access;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArmorStand.class)
public interface ArmorStandAccessor {
    @Invoker("getClickedSlot")
    EquipmentSlot invokeGetClickedSlot(Vec3 vec3);
}
