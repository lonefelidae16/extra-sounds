package dev.stashy.extrasounds.logics.entry;

import com.google.gson.*;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.SoundManager;
import dev.stashy.extrasounds.logics.debug.DebugUtils;
import dev.stashy.extrasounds.logics.json.SoundEntrySerializer;
import dev.stashy.extrasounds.logics.json.VersionedSoundSerializer;
import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundEventWrapper;
import dev.stashy.extrasounds.logics.runtime.VersionedSoundWrapper;
import dev.stashy.extrasounds.mapping.SoundDefinition;
import dev.stashy.extrasounds.mapping.SoundGenerator;
import dev.stashy.extrasounds.sounds.SoundType;
import dev.stashy.extrasounds.sounds.Sounds;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class SoundPackLoader {
    private static final int CACHE_VERSION = 1;
    private static final Identifier SOUNDS_JSON_ID = ExtraSounds.generateIdentifier("sounds.json");
    private static final String CACHE_FNAME = ExtraSounds.MODID + ".cache";
    private static final Path CACHE_PATH = Path.of(System.getProperty("java.io.tmpdir"), ".minecraft_fabric", CACHE_FNAME);

    private static final Map<Identifier, VersionedSoundEventWrapper> AUTO_GEN_SOUND_EVENT = new HashMap<>();
    private static final Map<Identifier, VersionedSoundEventWrapper> EXTERNAL_SOUND_EVENT = new HashMap<>();
    private static final Map<Identifier, VersionedSoundEventWrapper> CUSTOM_SOUND_EVENT = new HashMap<>();
    public static final VersionedClientResource EXTRA_SOUNDS_RESOURCE = Objects.requireNonNull(
            VersionedClientResource.newInstance(ExtraSounds.MODID, "%s Runtime Resources".formatted(ExtraSounds.class.getSimpleName()))
    );
    public static final Logger LOGGER = LogManager.getLogger(
            SoundPackLoader.class,
            new PrefixableMessageFactory("%s/%s".formatted(
                    ExtraSounds.class.getSimpleName(),
                    SoundPackLoader.class.getSimpleName()
            ))
    );

    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SoundEventRegistration.class, new SoundEntrySerializer())
            .registerTypeHierarchyAdapter(VersionedSoundWrapper.class, Objects.requireNonNull(VersionedSoundSerializer.newInstance()))
            .create();

    /**
     * Initialization of customized sound events.<br>
     * The cache file stored at {@link SoundPackLoader#CACHE_PATH} will be used.
     * If it is absent or invalid, the file will be regenerated.<br>
     * If the regeneration time is over 1000 milliseconds, it may be needed to refactor.
     */
    public static void init() {
        final long start = System.currentTimeMillis();
        final Map<String, SoundGenerator> soundGenMappers = new HashMap<>();
        final List<String> generatorVer = new ArrayList<>();

        // Collect entry points from mods.
        final var containers = FabricLoader.getInstance().getEntrypointContainers(ExtraSounds.MODID, SoundGenerator.class);
        containers.forEach(container -> {
            final SoundGenerator generator = container.getEntrypoint();
            if (generator == null || generator.itemSoundGenerator == null) {
                return;
            }

            final String namespace;
            try {
                namespace = container.getProvider().getMetadata().getId();
                if (namespace == null || namespace.isBlank()) {
                    throw new Exception("namespace is invalid: null or blank");
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to read mod metadata, ignoring.", ex);
                return;
            }
            if (DebugUtils.DEBUG) {
                LOGGER.info("registering generator with namespace '{}'", namespace);
            }
            soundGenMappers.put(namespace, generator);
            generatorVer.add(CacheInfo.getModVersion(container.getProvider()));
        });

        // Register the vanilla generator.
        soundGenMappers.put(Identifier.DEFAULT_NAMESPACE, BaseVanillaGenerator.GENERATOR);
        generatorVer.add(CacheInfo.getModVersion(FabricLoader.getInstance().getModContainer(Identifier.DEFAULT_NAMESPACE).orElseThrow()));

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
            jsonObject.keySet().forEach(key -> putSoundEvent(ExtraSounds.generateIdentifier(ExtraSounds.MODID, key)));
        } catch (Exception ex) {
            // If there is an exception, regenerate and write the cache.
            if (DebugUtils.DEBUG) {
                LOGGER.info("{}: {}", ex.getClass().getSimpleName(), ex.getMessage());
            }
            LOGGER.info("Regenerating cache...");
            final Map<String, SoundEventRegistration> resourceMapper = new HashMap<>();
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
        } else if (DebugUtils.DEBUG) {
            LOGGER.info("init finished; took {}ms.", tookMillis);
        }
        LOGGER.info("Generated sound pack successfully loaded; {} entries.", AUTO_GEN_SOUND_EVENT.size());
    }

    /**
     * Processes for the all items.<br>
     * This method is "Memory Sensitive" as creates 3x {@link SoundEventRegistration}s per item,
     * and avoid using Stream APIs in non-debug mode as much as possible.
     *
     * @param soundGenerator The information of generator including namespace and {@link SoundGenerator}.
     * @param resource       The {@link Map} of resource that the SoundEntry will be stored.
     */
    private static void processSounds(Map<String, SoundGenerator> soundGenerator, Map<String, SoundEventRegistration> resource) {
        final SoundEventRegistration fallbackSoundEntry = Sounds.aliased(SoundManager.FALLBACK_SOUND_EVENT);
        final Set<String> inSoundsJsonIds = new HashSet<>();
        final String fallbackSoundJson = GSON.toJson(fallbackSoundEntry);
        if (DebugUtils.SEARCH_UNDEF_SOUND) {
            try (InputStream stream = SoundPackLoader.class.getClassLoader().getResourceAsStream("assets/%s/%s".formatted(ExtraSounds.MODID, SOUNDS_JSON_ID.getPath()))) {
                Objects.requireNonNull(stream);
                final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                final JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining())).getAsJsonObject();
                inSoundsJsonIds.addAll(jsonObject.keySet());
            } catch (Exception ex) {
                LOGGER.warn("cannot open ExtraSounds' {}.", SOUNDS_JSON_ID.getPath(), ex);
            }
        }

        // process for registered item.
        for (Item item : ExtraSounds.MAIN.getItemRegistry()) {
            final Identifier itemId = ExtraSounds.MAIN.getItemId(item);
            final SoundDefinition definition;
            if (soundGenerator.containsKey(itemId.getNamespace())) {
                definition = soundGenerator.get(itemId.getNamespace()).itemSoundGenerator.apply(item);
            } else if (item instanceof BlockItem blockItem) {
                SoundDefinition blockSoundDef = SoundDefinition.of(fallbackSoundEntry);
                try {
                    final BlockState blockState = blockItem.getBlock().defaultBlockState();
                    final VersionedSoundEventWrapper blockSound = VersionedSoundEventWrapper.fromBlockState(blockState);
                    blockSoundDef = SoundDefinition.of(Sounds.aliased(blockSound));
                } catch (Exception ignored) {
                }
                definition = blockSoundDef;
            } else {
                definition = SoundDefinition.of(fallbackSoundEntry);
            }

            final Identifier grabId = ExtraSounds.getClickId(itemId, SoundType.GRAB);
            final SoundDefinition filled = definition.fill(Sounds.aliased(ExtraSounds.createEvent(grabId)));
            generateSoundEntry(grabId, filled.pickup, resource);
            generateSoundEntry(ExtraSounds.getClickId(itemId, SoundType.PLACE), filled.place, resource);
            generateSoundEntry(ExtraSounds.getClickId(itemId, SoundType.HOTBAR), filled.hotbar, resource);

            if (DebugUtils.SEARCH_UNDEF_SOUND) {
                final boolean isFallbackSoundEntry = Objects.equals(GSON.toJson(definition.pickup), fallbackSoundJson);
                final boolean notIncludeSoundsJson = !inSoundsJsonIds.contains(grabId.getPath());
                if (isFallbackSoundEntry && notIncludeSoundsJson) {
                    LOGGER.warn("unregistered sound was found: '{}'", itemId);
                }
            }
        }
    }

    /**
     * Generates a resource.
     *
     * @param clickId  Target id.
     * @param entry    Target {@link SoundEventRegistration}.
     * @param resource {@link Map} of resource that the SoundEntry will be stored.
     */
    private static void generateSoundEntry(Identifier clickId, SoundEventRegistration entry, Map<String, SoundEventRegistration> resource) {
        resource.put(clickId.getPath(), entry);
        putSoundEvent(clickId);
    }

    /**
     * Creates and Registers the SoundEvent from specified {@link Identifier}.
     *
     * @param clickId Target id.
     */
    private static void putSoundEvent(Identifier clickId) {
        AUTO_GEN_SOUND_EVENT.put(clickId, ExtraSounds.createEvent(clickId));
    }

    private static void putExternalSoundEvent(Identifier identifier) {
        EXTERNAL_SOUND_EVENT.put(identifier, ExtraSounds.createEvent(identifier));
    }

    public static Optional<VersionedSoundEventWrapper> getSoundEventById(Identifier... ids) {
        if (ids == null) {
            return Optional.empty();
        }

        for (Identifier target : ids) {
            if (CUSTOM_SOUND_EVENT.containsKey(target)) {
                return Optional.of(CUSTOM_SOUND_EVENT.get(target));
            }
        }
        return Optional.empty();
    }

    public static void reloadExternalSoundEvent() {
        EXTERNAL_SOUND_EVENT.clear();
        CUSTOM_SOUND_EVENT.clear();

        for (var pack : Minecraft.getInstance().getResourceManager().getResourceStack(SOUNDS_JSON_ID)) {
            if (pack.sourcePackId().equals(ExtraSounds.MODID)) {
                // Avoid auto-gen resource via SoundPackLoader.
                continue;
            }
            try (InputStream stream = pack.open()) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));
                final JsonObject jsonObject = JsonParser.parseString(reader.lines().collect(Collectors.joining())).getAsJsonObject();
                for (String idStr : jsonObject.keySet()) {
                    putExternalSoundEvent(ExtraSounds.generateIdentifier(ExtraSounds.MODID, idStr));
                }
            } catch (Exception ignored) {
            }
        }

        CUSTOM_SOUND_EVENT.putAll(AUTO_GEN_SOUND_EVENT);
        if (!EXTERNAL_SOUND_EVENT.isEmpty()) {
            LOGGER.info("External sound pack was found; {} entries.", EXTERNAL_SOUND_EVENT.size());
            CUSTOM_SOUND_EVENT.putAll(EXTERNAL_SOUND_EVENT);
        }
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
            return new CacheInfo(CACHE_VERSION, ExtraSounds.MAIN.getItemRegistry().size(), info);
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

        @Override
        @NotNull
        public String toString() {
            final CharSequence[] data = new CharSequence[]{
                    String.valueOf(version), String.valueOf(itemCount), String.join(DELIMITER_MOD_INFO, modInfo)
            };
            return String.join(DELIMITER_HEAD, data);
        }

        /**
         * Generates the version String from specified {@link ModContainer}.
         *
         * @param container Target.
         * @return Generated String.
         */
        private static String getModVersion(ModContainer container) {
            try {
                final ModMetadata metadata = container.getMetadata();
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
    static final class CacheData {
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
         * @param map  The cache data that will be converted to JSON.
         */
        static void create(CacheInfo info, Map<String, SoundEventRegistration> map) {
            try (BufferedWriter writer = Files.newBufferedWriter(CACHE_PATH)) {
                writer.write(info.toString().trim());
                writer.newLine();
                GSON.toJson(map, writer);
                writer.flush();
                if (DebugUtils.DEBUG) {
                    LOGGER.info("Cache saved at {}", CACHE_PATH.toAbsolutePath());
                }
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
