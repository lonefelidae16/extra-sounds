package dev.stashy.extrasounds.logics.json;

import com.google.gson.JsonSerializer;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundWrapper;
import me.lonefelidae16.groominglib.api.McVersionInterchange;

import java.lang.reflect.Constructor;

public abstract class VersionedSoundSerializer implements JsonSerializer<VersionedSoundWrapper> {
    public static VersionedSoundSerializer newInstance() {
        try {
            Class<VersionedSoundSerializer> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "json.SoundSerializer");
            Constructor<VersionedSoundSerializer> init = clazz.getConstructor();
            return init.newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'SoundSerializer'", ex);
        }
        return null;
    }
}
