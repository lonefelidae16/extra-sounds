package dev.stashy.extrasounds.mc26_2.compat;

import dev.stashy.extrasounds.mc26_2.VersionedMixinPlugin;
import net.fabricmc.loader.api.FabricLoader;

public class VersionedCompatMixinPlugin extends VersionedMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!super.shouldApplyMixin(targetClassName, mixinClassName)) {
            return false;
        }

        if (mixinClassName.contains(VersionedCompatMixinPlugin.class.getPackageName() + ".mixin.rei")) {
            return FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
        }

        return false;
    }
}
