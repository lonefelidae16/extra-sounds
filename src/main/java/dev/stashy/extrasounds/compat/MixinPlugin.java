package dev.stashy.extrasounds.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class MixinPlugin implements IMixinConfigPlugin {
    private static final boolean B_SNAPSHOT = isSnapshotVersion();

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (B_SNAPSHOT) {
            return false;
        }
        return mixinClassName.contains("dev.stashy.extrasounds.compat.mixin.rei") && FabricLoader.getInstance().isModLoaded("roughlyenoughitems");
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    private static boolean isSnapshotVersion() {
        try {
            var minecraft = FabricLoader.getInstance().getModContainer(Identifier.DEFAULT_NAMESPACE).orElseThrow();
            String gameVersion = minecraft.getMetadata().getVersion().toString();
            if (gameVersion.contains("-alpha")) {
                return true;
            } else if (gameVersion.contains("-beta")) {
                return true;
            } else if (gameVersion.contains("-rc")) {
                return true;
            }
        } catch (Throwable ignored) {
            return true;
        }
        return false;
    }
}
