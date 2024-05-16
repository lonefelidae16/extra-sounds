package dev.stashy.extrasounds.mixin.resource;

import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.mapping.SoundPackLoader;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {
    @Shadow
    private @Final ResourceType type;

    @ModifyVariable(method = "reload", at = @At("HEAD"))
    private List<ResourcePack> extrasounds$registerResPack(List<ResourcePack> arg3) {
        if (this.type != ResourceType.CLIENT_RESOURCES) {
            return arg3;
        }

        ExtraSounds.LOGGER.info("registering Runtime ResPack");
        List<ResourcePack> modifiable = new ArrayList<>(arg3);
        modifiable.addFirst(SoundPackLoader.EXTRA_SOUNDS_RESOURCE);
        return modifiable;
    }
}
