package dev.stashy.extrasounds.logics.compat;

import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class CompatibilityMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return "";
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (McVersionInterchange.isSnapshotVersion()) {
            return false;
        }

        if (mixinClassName.contains("dev.stashy.extrasounds.logics.compat.mixin.rei")) {
            return FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
        }
        if (mixinClassName.contains("dev.stashy.extrasounds.logics.compat.mixin.midnightcontrols")) {
            return FabricLoader.getInstance().isModLoaded("midnightcontrols");
        }

        return false;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return List.of();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
