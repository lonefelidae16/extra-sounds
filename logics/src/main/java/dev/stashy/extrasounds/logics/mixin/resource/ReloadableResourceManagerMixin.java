package dev.stashy.extrasounds.logics.mixin.resource;

import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.entry.SoundPackLoader;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;
import java.util.List;

@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin {
    @Shadow
    private @Final PackType type;

    @ModifyVariable(method = "createReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/CloseableResourceManager;close()V", shift = At.Shift.AFTER), ordinal = 0)
    private List<PackResources> extrasounds$registerResPack(List<PackResources> arg3) {
        if (this.type != PackType.CLIENT_RESOURCES) {
            return arg3;
        }

        ExtraSounds.LOGGER.info("registering Runtime ResPack");
        List<PackResources> modifiable = new LinkedList<>(arg3);
        modifiable.addFirst((PackResources) SoundPackLoader.EXTRA_SOUNDS_RESOURCE);
        return modifiable;
    }

    @Inject(method = "createReload", at = @At("RETURN"))
    private void extrasounds$afterReload(CallbackInfoReturnable<ReloadInstance> cir) {
        SoundPackLoader.reloadExternalSoundEvent();
    }
}
