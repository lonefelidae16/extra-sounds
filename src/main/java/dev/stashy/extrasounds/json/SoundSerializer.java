package dev.stashy.extrasounds.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.client.sound.Sound;

import java.lang.reflect.Type;

public class SoundSerializer implements JsonSerializer<Sound>
{
    @Override
    public JsonElement serialize(Sound src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject o = new JsonObject();
        o.addProperty("name", src.getIdentifier().toString());
        if (src.getVolume() != 1)
            o.addProperty("volume", src.getVolume());
        if (src.getPitch() != 1)
            o.addProperty("pitch", src.getPitch());
        if (src.getWeight() != 1)
            o.addProperty("weight", src.getWeight());
        if (src.getRegistrationType() != Sound.RegistrationType.FILE)
            o.addProperty("type", "event");
        if (src.isStreamed())
            o.addProperty("stream", src.isStreamed());
        if (src.isPreloaded())
            o.addProperty("preload", src.isPreloaded());
        if (src.getAttenuation() != 16)
            o.addProperty("attenuation_distance", src.getAttenuation());
        return o;
    }
}
