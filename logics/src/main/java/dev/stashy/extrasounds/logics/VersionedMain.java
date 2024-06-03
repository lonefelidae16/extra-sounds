package dev.stashy.extrasounds.logics;

import me.lonefelidae16.groominglib.api.McVersionInterchange;
import net.minecraft.util.Identifier;

import java.lang.reflect.Constructor;

public abstract class VersionedMain {
    public static VersionedMain newInstance() {
        try {
            Class<VersionedMain> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE,"Main");
            Constructor<VersionedMain> init = clazz.getConstructor();
            return init.newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'SoundManager'", ex);
        }
        return null;
    }

    public abstract Identifier generateIdentifier(String namespace, String path);
}
