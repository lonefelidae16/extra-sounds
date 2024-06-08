package dev.stashy.extrasounds.mc1_18.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import dev.stashy.extrasounds.logics.json.VersionedSoundSerializer;
import dev.stashy.extrasounds.logics.sounds.VersionedSoundWrapper;
import net.minecraft.client.sound.Sound;

import java.lang.reflect.Type;

public class SoundSerializer extends VersionedSoundSerializer {
    @Override
    public JsonElement serialize(VersionedSoundWrapper src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject o = new JsonObject();
        o.addProperty("name", src.getIdentifierImpl().toString());
        if (src.getVolumeImpl() instanceof Float value && value != 1) {
            o.addProperty("volume", value);
        }
        if (src.getPitchImpl() instanceof Float value && value != 1) {
            o.addProperty("pitch", value);
        }
        if (src.getWeightImpl() != 1) {
            o.addProperty("weight", src.getWeightImpl());
        }
        if (src.getRegistrationTypeImpl() != Sound.RegistrationType.FILE) {
            o.addProperty("type", "event");
        }
        if (src.isStreamedImpl()) {
            o.addProperty("stream", src.isStreamedImpl());
        }
        if (src.isPreloadedImpl()) {
            o.addProperty("preload", src.isPreloadedImpl());
        }
        if (src.getAttenuationImpl() != 16) {
            o.addProperty("attenuation_distance", src.getAttenuationImpl());
        }
        return o;
    }
}
