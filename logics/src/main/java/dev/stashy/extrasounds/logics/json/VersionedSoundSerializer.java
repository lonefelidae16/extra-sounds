package dev.stashy.extrasounds.logics.json;

import com.google.gson.JsonSerializer;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundWrapper;
import me.lonefelidae16.groominglib.api.McVersionInterchange;

public abstract class VersionedSoundSerializer implements JsonSerializer<VersionedSoundWrapper> {
    public static VersionedSoundSerializer newInstance() {
        try {
            Class<VersionedSoundSerializer> clazz = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "json.SoundSerializer");
            return clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Cannot initialize 'SoundSerializer'", ex);
        }
        return null;
    }
}
