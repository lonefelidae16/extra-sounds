package dev.stashy.extrasounds.logics.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.client.sound.Sound;
import net.minecraft.util.math.random.Random;

import java.lang.reflect.Type;

public class SoundSerializer implements JsonSerializer<Sound>
{
    Random r = Random.create();

    @Override
    public JsonElement serialize(Sound src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject o = new JsonObject();
        o.addProperty("name", src.getIdentifier().toString());
        if (src.getVolume().get(r) != 1)
            o.addProperty("volume", src.getVolume().get(r));
        if (src.getPitch().get(r) != 1)
            o.addProperty("pitch", src.getPitch().get(r));
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
