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
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class SoundPackLoader {
    private static final int CACHE_VERSION = 1;
    private static final RuntimeResourcePack EXTRA_SOUNDS_RESOURCE = RuntimeResourcePack.create(ExtraSounds.MODID);
    private static final Identifier SOUNDS_JSON_ID = new Identifier(ExtraSounds.MODID, "sounds.json");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String CACHE_FNAME = ExtraSounds.MODID + ".cache";
    private static final Path CACHE_PATH = Path.of(System.getProperty("java.io.tmpdir"), ".minecraft_fabric", CACHE_FNAME);

    public static final Map<Identifier, SoundEvent> CUSTOM_SOUND_EVENT = new HashMap<>();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SoundEntry.class, new SoundEntrySerializer())
            .registerTypeAdapter(Sound.class, new SoundSerializer())
            .create();

    /**
     * Initialization of customized sound event.<br>
     * The cache file stored at {@link SoundPackLoader#CACHE_PATH} will be used.
     * If it is absent or invalid, the file will be regenerated.<br>
     * If the regeneration time over 1,000 milliseconds, it may be needed to refactor.
     */
    public static void init() {
        final long start = System.currentTimeMillis();
        final Map<String, SoundGenerator> soundGenMappers = new HashMap<>();

        final List<EntrypointContainer<SoundGenerator>> containers = FabricLoader.getInstance().getEntrypointContainers(ExtraSounds.MODID, SoundGenerator.class);

        containers.forEach(container -> {
            final SoundGenerator generator = container.getEntrypoint();
            // FIXME: When duplicate namespace declared from 2 or more mods, the last mod takes priority.
            soundGenMappers.put(generator.namespace, generator);
        });
        final String[] generatorVer = containers.stream().map(CacheInfo::getModVersion).toArray(String[]::new);
        final CacheInfo currentCacheInfo = CacheInfo.of(generatorVer);

        // Read from cache.
        try {
            Files.createDirectories(CACHE_PATH.getParent());

            if (!Files.exists(CACHE_PATH)) {
                throw new FileNotFoundException("Cache does not exist.");
            }

            if (DebugUtils.noCache) {
                throw new RuntimeException("JVM arg '%s' is detected.".formatted(DebugUtils.noCacheVar));
            }

            final CacheData cacheData = CacheData.read();
            if (!cacheData.info.equals(currentCacheInfo)) {
                throw new InvalidObjectException("Incorrect cache info.");
            }

            final JsonObject jsonObject = cacheData.asJsonObject();
            jsonObject.keySet().forEach(key -> putSoundEvent(new Identifier(ExtraSounds.MODID, key)));
        } catch (Throwable ex) {
            // If there is an exception, regenerate and write the cache.
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
        DebugUtils.genericLog("%s init finished; took %dms.".formatted(SoundPackLoader.class.getSimpleName(), finish - start));
        LOGGER.info("[{}] sound pack successfully loaded; {} entries.", ExtraSounds.class.getSimpleName(), CUSTOM_SOUND_EVENT.keySet().size());
    }

    /**
     * Processes for the all items.<br>
     * This method is "Memory Sensitive" as creates 3x {@link SoundEntry}s per item,
     * and avoid using the Stream APIs in non-debug mode as much as possible.
     *
     * @param soundGenerator The information of generator including namespace and {@link SoundGenerator}.
     * @param resource       The {@link Map} of resource that the SoundEntry will be stored.
     */
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

    /**
     * Generates the resource.
     *
     * @param itemId       Target item id.
     * @param type         The {@link SoundType} which category of volume to play.
     * @param entry        Target {@link SoundEntry}.
     * @param defaultEntry The fallback SoundEntry.
     * @param resource     The {@link Map} of resource that the SoundEntry will be stored.
     */
    private static void generateSoundEntry(Identifier itemId, SoundType type, SoundEntry entry, SoundEntry defaultEntry, Map<String, SoundEntry> resource) {
        final SoundEntry soundEntry = (entry == null) ? defaultEntry : entry;
        final Identifier id = ExtraSounds.getClickId(itemId, type);
        resource.put(id.getPath(), soundEntry);
        putSoundEvent(id);
    }

    /**
     * Creates and Registers the {@link SoundEvent} from specified {@link Identifier}.
     *
     * @param clickId Target id.
     */
    private static void putSoundEvent(Identifier clickId) {
        CUSTOM_SOUND_EVENT.put(clickId, SoundEvent.of(clickId));
    }

    /**
     * Shows the information of the cache.<br>
     * This is used at the first line in the file defined by {@link SoundPackLoader#CACHE_FNAME}.
     *
     * @param version   The cache version.
     * @param itemCount The number of the Item Registry.
     * @param info      The String array of mod ids.
     */
    record CacheInfo(int version, int itemCount, String[] info) {
        private static final String DELIMITER_MOD_INFO = ",";
        private static final String DELIMITER_HEAD = ";";

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
                var arr = string.split(DELIMITER_HEAD);
                return new CacheInfo(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), arr[2].split(DELIMITER_MOD_INFO));
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
            final CharSequence[] data = new CharSequence[]{
                    String.valueOf(version), String.valueOf(itemCount), String.join(DELIMITER_MOD_INFO, info)
            };
            return String.join(DELIMITER_HEAD, data);
        }

        /**
         * Generates the version String from specified {@link EntrypointContainer}.
         *
         * @param container Target.
         * @return Generated String.
         */
        private static String getModVersion(EntrypointContainer<SoundGenerator> container) {
            if (container == null) {
                return "<NULL>";
            }

            final ModMetadata metadata = container.getProvider().getMetadata();
            if (metadata == null) {
                return "<NULL>";
            }

            final String modId = metadata.getId();
            final String modVer = metadata.getVersion().getFriendlyString();
            return validate("%s %s".formatted(modId, modVer));
        }

        private static String validate(String in) {
            return in.replaceAll("[%s%s]".formatted(DELIMITER_HEAD, DELIMITER_MOD_INFO), "_");
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
            try (BufferedReader reader = Files.newBufferedReader(CACHE_PATH)) {
                final CacheInfo cacheInfo = CacheInfo.fromString(reader.readLine().trim());
                final StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                return new CacheData(cacheInfo, builder);
            } catch (Throwable ex) {
                LOGGER.error("[%s] Failed to load ExtraSounds cache.".formatted(ExtraSounds.class.getSimpleName()), ex);
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
            try (BufferedWriter writer = Files.newBufferedWriter(CACHE_PATH)) {
                writer.write(info.toString().trim());
                writer.newLine();
                GSON.toJson(map, writer);
                writer.flush();
                DebugUtils.genericLog("Cache saved at %s".formatted(CACHE_PATH.toAbsolutePath()));
            } catch (Throwable ex) {
                LOGGER.error("[%s] Failed to save the cache.".formatted(ExtraSounds.class.getSimpleName()), ex);
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
