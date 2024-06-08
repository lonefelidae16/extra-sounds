package dev.stashy.extrasounds.mc1_20.mixin.resource;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.mapping.SoundPackLoader;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.LinkedList;
import java.util.List;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
    @Shadow
    private @Final ResourceType type;

    @ModifyVariable(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/LifecycledResourceManager;close()V", shift = At.Shift.AFTER), ordinal = 0)
    private List<ResourcePack> extrasounds$registerResPack(List<ResourcePack> arg3) {
        if (this.type != ResourceType.CLIENT_RESOURCES) {
            return arg3;
        }

        ExtraSounds.LOGGER.info("registering Runtime ResPack");
        List<ResourcePack> modifiable = new LinkedList<>(arg3);
        modifiable.add(0, (ResourcePack) SoundPackLoader.EXTRA_SOUNDS_RESOURCE);
        return modifiable;
    }
}
