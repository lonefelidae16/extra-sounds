package dev.stashy.extrasounds.logics.json;

import com.google.gson.*;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;

import java.lang.reflect.Type;
import java.util.Objects;

public class SoundEntrySerializer implements JsonSerializer<SoundEntry> {
    @Override
    public JsonElement serialize(SoundEntry src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject o = new JsonObject();
        JsonArray sounds = new JsonArray();
        for (Sound snd : src.getSounds())
            sounds.add(context.serialize(snd));
        o.add("sounds", sounds);
        if (src.canReplace())
            o.addProperty("replace", src.canReplace());
        if (!Objects.equals(src.getSubtitle(), ""))
            o.addProperty("subtitle", src.getSubtitle());
        return o;
    }
}
