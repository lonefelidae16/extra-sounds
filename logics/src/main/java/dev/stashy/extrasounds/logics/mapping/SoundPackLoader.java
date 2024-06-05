package dev.stashy.extrasounds.logics.mapping;

import com.google.common.collect.Lists;
import com.google.gson.*;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.VersionedSoundManager;
import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.json.SoundEntrySerializer;
import dev.stashy.extrasounds.logics.json.SoundSerializer;
import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import dev.stashy.extrasounds.logics.sounds.SoundType;
import dev.stashy.extrasounds.logics.sounds.Sounds;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.block.BlockState;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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
    private static final Identifier SOUNDS_JSON_ID = ExtraSounds.generateIdentifier("sounds.json");
    private static final String CACHE_FNAME = ExtraSounds.MODID + ".cache";
    private static final Path CACHE_PATH = Path.of(System.getProperty("java.io.tmpdir"), ".minecraft_fabric", CACHE_FNAME);

    public static final Map<Identifier, SoundEvent> CUSTOM_SOUND_EVENT = new HashMap<>();
    public static final VersionedClientResource EXTRA_SOUNDS_RESOURCE = Objects.requireNonNull(VersionedClientResource.newInstance(ExtraSounds.MODID, "%s Runtime Resources".formatted(ExtraSounds.class.getSimpleName())));
    public static final Logger LOGGER = LogManager.getLogger(
            SoundPackLoader.class,
            new PrefixableMessageFactory("%s/%s".formatted(
                    ExtraSounds.class.getSimpleName(),
                    SoundPackLoader.class.getSimpleName()
            ))
    );

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SoundEntry.class, new SoundEntrySerializer())
            .registerTypeHierarchyAdapter(Sound.class, new SoundSerializer())
            .create();

    /**
     * Initialization of customized sound event.<br>
     * The cache file stored at {@link SoundPackLoader#CACHE_PATH} will be used.
     * If it is absent or invalid, the file will be regenerated.<br>
     * If the regeneration time over 1000 milliseconds, it may be needed to refactor.
     */
    public static void init() {
        final long start = System.currentTimeMillis();
        final Map<String, SoundGenerator> soundGenMappers = new HashMap<>();
        final List<String> generatorVer = new ArrayList<>();

        final List<EntrypointContainer<SoundGenerator>> containers = FabricLoader.getInstance().getEntrypointContainers(ExtraSounds.MODID, SoundGenerator.class);

        containers.forEach(container -> {
            final SoundGenerator generator = container.getEntrypoint();
            if (generator == null || generator.namespace == null || generator.itemSoundGenerator == null) {
                return;
            }

            final String namespace;
            if (generator.namespace.isEmpty()) {
                try {
                    namespace = container.getProvider().getMetadata().getId();
                    if (namespace == null || namespace.isBlank()) {
                        throw new Exception("namespace is invalid: %s".formatted(namespace));
                    }
                } catch (Exception ex) {
                    LOGGER.error("Failed to read mod metadata, ignoring.", ex);
                    return;
                }
            } else {
                // FIXME: When duplicate namespace declared from 2 or more mods, the last mod takes priority.
                namespace = generator.namespace;
            }
            DebugUtils.genericLog("registering generator with namespace '%s'".formatted(namespace));
            soundGenMappers.put(namespace, generator);
            generatorVer.add(CacheInfo.getModVersion(container));
        });
        final CacheInfo currentCacheInfo = CacheInfo.of(generatorVer.toArray(new String[0]));

        // Read from cache.
        try {
            Files.createDirectories(CACHE_PATH.getParent());

            if (!Files.exists(CACHE_PATH)) {
                throw new FileNotFoundException("Cache does not exist.");
            }

            if (DebugUtils.NO_CACHE) {
                throw new RuntimeException("JVM arg '%s' is detected.".formatted(DebugUtils.NO_CACHE_VAR));
            }

            final CacheData cacheData = CacheData.read();
            if (!cacheData.info.equals(currentCacheInfo)) {
                throw new InvalidObjectException("Incorrect cache info.");
            }

            final JsonObject jsonObject = cacheData.asJsonObject();
            jsonObject.keySet().forEach(key -> putSoundEvent(ExtraSounds.generateIdentifier(key)));
        } catch (Exception ex) {
            // If there is an exception, regenerate and write the cache.
            DebugUtils.genericLog(ex.getMessage());
            LOGGER.info("Regenerating cache...");
            final Map<String, SoundEntry> resourceMapper = new HashMap<>();
            processSounds(soundGenMappers, resourceMapper);
            CacheData.create(currentCacheInfo, resourceMapper);
        }

        if (DebugUtils.DEBUG) {
            DebugUtils.exportSoundsJson(CacheData.read().asJsonBytes());
            DebugUtils.exportGenerators(soundGenMappers);
        }

        EXTRA_SOUNDS_RESOURCE.addResourceAsync(SOUNDS_JSON_ID, identifier -> CacheData.read().asJsonBytes());
        final long tookMillis = System.currentTimeMillis() - start;
        if (tookMillis >= 1000) {
            LOGGER.warn("init took too long; {}ms.", tookMillis);
        } else {
            DebugUtils.genericLog("init finished; took %dms.".formatted(tookMillis));
        }
        LOGGER.info("sound pack successfully loaded; {} entries.", CUSTOM_SOUND_EVENT.keySet().size());
    }

    /**
     * Processes for the all items.<br>
     * This method is "Memory Sensitive" as creates 3x {@link SoundEntry}s per item,
     * and avoid using Stream APIs in non-debug mode as much as possible.
     *
     * @param soundGenerator The information of generator including namespace and {@link SoundGenerator}.
     * @param resource       The {@link Map} of resource that the SoundEntry will be stored.
     */
    private static void processSounds(Map<String, SoundGenerator> soundGenerator, Map<String, SoundEntry> resource) {
        final SoundEntry fallbackSoundEntry = Sounds.aliased(VersionedSoundManager.FALLBACK_SOUND_EVENT);
        final List<String> inSoundsJsonIds = Lists.newArrayList();
        final String fallbackSoundJson = GSON.toJson(fallbackSoundEntry);
        if (DebugUtils.SEARCH_UNDEF_SOUND) {
            try (InputStream stream = SoundPackLoader.class.getClassLoader().getResourceAsStream("assets/extrasounds/sounds.json")) {
                Objects.requireNonNull(stream);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                final JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining())).getAsJsonObject();
                inSoundsJsonIds.addAll(jsonObject.keySet());
            } catch (Exception ex) {
                LOGGER.warn("cannot open ExtraSounds' sounds.json.", ex);
            }
        }

        for (Item item : ExtraSounds.getItemRegistry()) {
            final Identifier itemId = ExtraSounds.fromItemRegistry(item);
            final SoundDefinition definition;
            if (soundGenerator.containsKey(itemId.getNamespace())) {
                definition = soundGenerator.get(itemId.getNamespace()).itemSoundGenerator.apply(item);
            } else if (item instanceof BlockItem blockItem) {
                SoundDefinition blockSoundDef = SoundDefinition.of(fallbackSoundEntry);
                try {
                    final BlockState blockState = blockItem.getBlock().getDefaultState();
                    final SoundEvent blockSound = blockState.getSoundGroup().getPlaceSound();
                    blockSoundDef = SoundDefinition.of(Sounds.aliased(blockSound));
                } catch (Exception ignored) {
                }
                definition = blockSoundDef;
            } else {
                definition = SoundDefinition.of(fallbackSoundEntry);
            }

            final Identifier pickupClickId = ExtraSounds.getClickId(itemId, SoundType.PICKUP);
            final SoundDefinition filled = definition.fill(Sounds.aliased(ExtraSounds.createEvent(pickupClickId)));
            generateSoundEntry(pickupClickId, filled.pickup, resource);
            generateSoundEntry(ExtraSounds.getClickId(itemId, SoundType.PLACE), filled.place, resource);
            generateSoundEntry(ExtraSounds.getClickId(itemId, SoundType.HOTBAR), filled.hotbar, resource);

            if (DebugUtils.SEARCH_UNDEF_SOUND) {
                final boolean isFallbackSoundEntry = Objects.equals(GSON.toJson(definition.pickup), fallbackSoundJson);
                final boolean notIncludeSoundsJson = !inSoundsJsonIds.contains(pickupClickId.getPath());
                if (isFallbackSoundEntry && notIncludeSoundsJson) {
                    LOGGER.warn("unregistered sound was found: '{}'", itemId);
                }
            }
        }
    }

    /**
     * Generates a resource.
     *
     * @param clickId      Target id.
     * @param entry        Target {@link SoundEntry}.
     * @param resource     {@link Map} of resource that the SoundEntry will be stored.
     */
    private static void generateSoundEntry(Identifier clickId, SoundEntry entry, Map<String, SoundEntry> resource) {
        resource.put(clickId.getPath(), entry);
        putSoundEvent(clickId);
    }

    /**
     * Creates and Registers the {@link SoundEvent} from specified {@link Identifier}.
     *
     * @param clickId Target id.
     */
    private static void putSoundEvent(Identifier clickId) {
        CUSTOM_SOUND_EVENT.put(clickId, ExtraSounds.createEvent(clickId));
    }

    /**
     * Shows the information of the cache.<br>
     * This is used at the first line in the file defined by {@link SoundPackLoader#CACHE_FNAME}.
     *
     * @param version   The cache version.
     * @param itemCount The number of the Item Registry.
     * @param modInfo   The String array of mod ids.
     */
    record CacheInfo(int version, int itemCount, String[] modInfo) {
        private static final String DELIMITER_MOD_INFO = ",";
        private static final String DELIMITER_HEAD = ";";

        /**
         * Creates new cache info from generator version info.
         *
         * @param info The array of String that include mod ids.
         * @return A new instance of {@link CacheInfo}.
         */
        public static CacheInfo of(String[] info) {
            return new CacheInfo(CACHE_VERSION, ExtraSounds.getItemRegistry().size(), info);
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
            } catch (Exception ignored) {
                return new CacheInfo(0, 0, new String[0]);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CacheInfo comp)
                return this.version == comp.version
                        && this.itemCount == comp.itemCount
                        && Arrays.equals(this.modInfo, comp.modInfo);
            return false;
        }

        public String toString() {
            final CharSequence[] data = new CharSequence[]{
                    String.valueOf(version), String.valueOf(itemCount), String.join(DELIMITER_MOD_INFO, modInfo)
            };
            return String.join(DELIMITER_HEAD, data);
        }

        /**
         * Generates the version String from specified {@link EntrypointContainer}.
         *
         * @param container Target.
         * @return Generated String.
         */
        private static String getModVersion(EntrypointContainer<?> container) {
            try {
                final ModMetadata metadata = container.getProvider().getMetadata();
                final String modId = metadata.getId();
                final String modVer = metadata.getVersion().getFriendlyString();
                return sanitize("%s %s".formatted(modId, modVer));
            } catch (Exception ex) {
                LOGGER.error("Failed to obtain mod info.", ex);
            }
            return "<NULL>";
        }

        private static String sanitize(String in) {
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
            } catch (IOException ex) {
                LOGGER.error("Failed to load ExtraSounds cache.", ex);
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
            } catch (IOException | JsonIOException ex) {
                LOGGER.error("Failed to save the cache.", ex);
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
