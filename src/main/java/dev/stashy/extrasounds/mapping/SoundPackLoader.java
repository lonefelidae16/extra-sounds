package dev.stashy.extrasounds.mapping;

import com.google.common.collect.Lists;
import com.google.gson.*;
import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.debug.DebugUtils;
import dev.stashy.extrasounds.json.SoundEntrySerializer;
import dev.stashy.extrasounds.json.SoundSerializer;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.stream.Collectors;

public class SoundPackLoader {
    private static final int CACHE_VERSION = 1;
    private static final RuntimeResourcePack EXTRA_SOUNDS_RESOURCE = RuntimeResourcePack.create(ExtraSounds.MODID);
    private static final Identifier SOUNDS_JSON_ID = new Identifier(ExtraSounds.MODID, "sounds.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final File CACHE_FILE = FabricLoader.getInstance().getConfigDir().resolve("extrasounds.cache").toFile();

    public static final Map<Identifier, SoundEvent> CUSTOM_SOUND_EVENT = new HashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SoundEntry.class, new SoundEntrySerializer())
            .registerTypeAdapter(Sound.class, new SoundSerializer())
            .create();

    public static void init() {
        final long start = System.currentTimeMillis();
        final Map<String, SoundGenerator> soundGenMappers = new HashMap<>();

        FabricLoader.getInstance().getEntrypoints(ExtraSounds.MODID, SoundGenerator.class)
                .forEach(it -> soundGenMappers.put(it.namespace, it));

        String[] generatorVer = soundGenMappers.values().stream().map(CacheInfo::getModVersion).toArray(String[]::new);
        final CacheInfo currentCacheInfo = CacheInfo.of(generatorVer);

        try {
            if (!CACHE_FILE.exists()) {
                throw new FileNotFoundException("Cache does not exist.");
            }

            if (DebugUtils.noCache) {
                throw new RuntimeException(String.format("JVM arg '%s' is detected.", DebugUtils.noCacheVar));
            }

            final CacheData cacheData = CacheData.read();
            if (!cacheData.info.equals(currentCacheInfo)) {
                throw new InvalidObjectException("Incorrect cache info.");
            }

            final JsonObject jsonObject = cacheData.asJsonObject();
            jsonObject.keySet().forEach(key -> putSoundEvent(new Identifier(ExtraSounds.MODID, key)));
        } catch (Throwable ex) {
            DebugUtils.genericLog(ex.getMessage());
            LOGGER.info("[{}] Regenerating cache...", ExtraSounds.class.getSimpleName());
            final Map<String, SoundEntry> resourceMapper = new HashMap<>();
            processSounds(soundGenMappers, resourceMapper);
            CacheData.create(currentCacheInfo, resourceMapper);
        }

        if (DebugUtils.debug) {
            DebugUtils.exportSoundsJson(CacheData.read().asJsonBytes());
            DebugUtils.exportGenerators(soundGenMappers);
        }

        EXTRA_SOUNDS_RESOURCE.addAsyncResource(ResourceType.CLIENT_RESOURCES, SOUNDS_JSON_ID, identifier -> CacheData.read().asJsonBytes());
        RRPCallback.BEFORE_VANILLA.register(packs -> packs.add(EXTRA_SOUNDS_RESOURCE));
        final long finish = System.currentTimeMillis();
        DebugUtils.genericLog(String.format("%s init finished; took %dms.", SoundPackLoader.class.getSimpleName(), finish - start));
        LOGGER.info("[{}] sound pack successfully loaded; {} entries.", ExtraSounds.class.getSimpleName(), CUSTOM_SOUND_EVENT.keySet().size());
    }

    private static void processSounds(Map<String, SoundGenerator> soundGenerator, Map<String, SoundEntry> resource) {
        final SoundEntry fallbackSoundEntry = Sounds.aliased(Sounds.ITEM_PICK);
        final List<String> inSoundsJsonIds = Lists.newArrayList();
        final String fallbackSoundJson = GSON.toJson(fallbackSoundEntry);
        if (DebugUtils.SEARCH_UNDEF_SOUND) {
            try (InputStream stream = SoundPackLoader.class.getClassLoader().getResourceAsStream("assets/extrasounds/sounds.json")) {
                Objects.requireNonNull(stream);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                final JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining())).getAsJsonObject();
                inSoundsJsonIds.addAll(jsonObject.keySet());
            } catch (Throwable ex) {
                LOGGER.warn("cannot open ExtraSounds' sounds.json.", ex);
            }
        }

        for (Item item : Registries.ITEM) {
            final Identifier itemId = Registries.ITEM.getId(item);
            final SoundDefinition definition;
            if (soundGenerator.containsKey(itemId.getNamespace())) {
                definition = soundGenerator.get(itemId.getNamespace()).itemSoundGenerator.apply(item);
            } else if (item instanceof BlockItem blockItem) {
                SoundDefinition blockSoundDef = SoundDefinition.of(fallbackSoundEntry);
                try {
                    final Block block = blockItem.getBlock();
                    final SoundEvent blockSound = block.getSoundGroup(block.getDefaultState()).getPlaceSound();
                    blockSoundDef = SoundDefinition.of(Sounds.aliased(blockSound));
                } catch (Throwable ignored) {
                }
                definition = blockSoundDef;
            } else {
                definition = SoundDefinition.of(fallbackSoundEntry);
            }

            final Identifier pickupSoundId = ExtraSounds.getClickId(itemId, SoundType.PICKUP);
            final SoundEntry pickupSoundEntry = Sounds.aliased(SoundEvent.of(pickupSoundId));
            generateSoundEntry(itemId, SoundType.PICKUP, definition.pickup, pickupSoundEntry, resource);
            generateSoundEntry(itemId, SoundType.PLACE, definition.place, pickupSoundEntry, resource);
            generateSoundEntry(itemId, SoundType.HOTBAR, definition.hotbar, pickupSoundEntry, resource);

            if (DebugUtils.SEARCH_UNDEF_SOUND) {
                final boolean isFallbackSoundEntry = Objects.equals(GSON.toJson(definition.pickup), fallbackSoundJson);
                final boolean notIncludeSoundsJson = !inSoundsJsonIds.contains(pickupSoundId.getPath());
                if (isFallbackSoundEntry && notIncludeSoundsJson) {
                    LOGGER.info("unregistered sound was found: '{}'", itemId);
                }
            }
        }
    }

    private static void generateSoundEntry(Identifier itemId, SoundType type, SoundEntry entry, SoundEntry defaultEntry, Map<String, SoundEntry> resource) {
        final SoundEntry soundEntry = (entry == null) ? defaultEntry : entry;
        final Identifier id = ExtraSounds.getClickId(itemId, type);
        resource.put(id.getPath(), soundEntry);
        putSoundEvent(id);
    }

    private static void putSoundEvent(Identifier clickId) {
        CUSTOM_SOUND_EVENT.put(clickId, SoundEvent.of(clickId));
    }

    /**
     * Shows the information of the cache.<br>
     * This is used in the file <code>extrasounds.cache</code> at the first line.
     *
     * @param version   The cache version.
     * @param itemCount The number of the Item Registry.
     * @param info      The mod id String array.
     */
    record CacheInfo(int version, int itemCount, String[] info) {
        /**
         * Creates new cache info from generator version info.
         *
         * @param info The array of String that include mod ids.
         * @return A new instance of {@link CacheInfo}.
         */
        public static CacheInfo of(String[] info) {
            return new CacheInfo(CACHE_VERSION, Registries.ITEM.size(), info);
        }

        /**
         * Parses to the {@link CacheInfo} from String.
         *
         * @param string The String.
         * @return A new instance of {@link CacheInfo}.
         */
        public static CacheInfo fromString(String string) {
            try {
                var arr = string.split(";");
                return new CacheInfo(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), arr[2].split(","));
            } catch (Throwable ignored) {
                return new CacheInfo(0, 0, new String[0]);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CacheInfo comp)
                return this.version == comp.version
                        && this.itemCount == comp.itemCount
                        && Arrays.equals(this.info, comp.info);
            return false;
        }

        public String toString() {
            return "%d;%d;%s".formatted(version, itemCount, String.join(",", info));
        }

        /**
         * Finds and Generates the version String from specified {@link SoundGenerator}.
         *
         * @param generator Target.
         * @return Generated String.
         */
        private static String getModVersion(SoundGenerator generator) {
            if (generator == null) {
                return "<NULL>";
            }

            final String modId = generator.modId;
            return FabricLoader.getInstance().getModContainer(modId)
                    .map(modContainer -> modId + " " + modContainer.getMetadata().getVersion().getFriendlyString())
                    .orElse("nope");
        }
    }

    /**
     * Shows the cache data that include {@link CacheInfo} and Json String.
     */
    protected static class CacheData {
        /**
         * The cache info.
         */
        private final CacheInfo info;
        /**
         * The cache data.
         */
        private final CharSequence json;

        private CacheData(CacheInfo info, CharSequence json) {
            this.info = info;
            this.json = json;
        }

        /**
         * Reads the cache data.
         *
         * @return The instance of {@link CacheData}.
         */
        static CacheData read() {
            try (Reader reader = new FileReader(CACHE_FILE)) {
                final BufferedReader bufferedReader = new BufferedReader(reader);
                final CacheInfo cacheInfo = CacheInfo.fromString(bufferedReader.readLine());
                final StringBuilder builder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
                return new CacheData(cacheInfo, builder);
            } catch (Throwable ex) {
                final String message = String.format("[%s] Failed to load ExtraSounds cache.", ExtraSounds.class.getSimpleName());
                LOGGER.error(message, ex);
            }
            return new CacheData(CacheInfo.of(new String[0]), "{}");
        }

        /**
         * Writes to the file.
         *
         * @param info The current cache info.
         * @param map  The cache data that will be converted to json.
         */
        static void create(CacheInfo info, Map<String, SoundEntry> map) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CACHE_FILE))) {
                writer.write(info.toString());
                writer.newLine();
                GSON.toJson(map, writer);
                writer.flush();
                DebugUtils.genericLog("Cache saved.");
            } catch (Throwable ex) {
                final String message = String.format("[%s] Failed to save the cache.", ExtraSounds.class.getSimpleName());
                LOGGER.error(message, ex);
            }
        }

        public JsonObject asJsonObject() throws JsonParseException {
            return JsonParser.parseString(this.json.toString()).getAsJsonObject();
        }

        public byte[] asJsonBytes() {
            return this.json.toString().getBytes();
        }
    }
}
