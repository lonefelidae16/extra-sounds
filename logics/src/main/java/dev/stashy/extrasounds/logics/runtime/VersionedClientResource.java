package dev.stashy.extrasounds.logics.runtime;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import dev.stashy.extrasounds.logics.ExtraSounds;
import me.lonefelidae16.groominglib.api.McVersionInterchange;
import me.lonefelidae16.groominglib.api.PrefixableMessageFactory;
import net.minecraft.resource.AbstractFileResourcePack;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Version-compatible class for {@link net.minecraft.resource.ResourcePack}.<br>
 * Needs to append prefix {@code *Impl} for each method as this class does not implement
 * {@link net.minecraft.resource.ResourcePack} directly and will not re-mapped.
 */
public abstract class VersionedClientResource {
    protected static final ExecutorService EXECUTOR_SERVICE;
    protected static final Logger LOGGER;

    static {
        int proc = Math.max(Runtime.getRuntime().availableProcessors() / 2 - 1, 1);
        LOGGER = LogManager.getLogger(
                VersionedClientResource.class,
                new PrefixableMessageFactory(
                        "%s/ResourcePack".formatted(ExtraSounds.class.getSimpleName())
                )
        );
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(proc, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ResPack-Workers-%s").build());
    }

    protected final int packVersion;
    protected CharSequence name;
    protected final Map<Identifier, Supplier<byte[]>> assets;

    public void addResourceAsync(Identifier location, Function<Identifier, byte[]> supplier) {
        var future = EXECUTOR_SERVICE.submit(() -> supplier.apply(location));
        this.assets.put(location, () -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    protected VersionedClientResource(String modId, String packName) {
        this.packVersion = 5;
        this.assets = new ConcurrentHashMap<>();
    }

    public static VersionedClientResource newInstance(String modId, String name) {
        try {
            Class<VersionedClientResource> instance = McVersionInterchange.getCompatibleClass(ExtraSounds.BASE_PACKAGE, "runtime.ClientResource");
            Constructor<VersionedClientResource> init = instance.getConstructor(String.class, String.class);
            return init.newInstance(modId, name);
        } catch (Exception ex) {
            ExtraSounds.LOGGER.error("Failed to initialize 'ClientResource'", ex);
        }
        return null;
    }

    public InputSupplier<InputStream> openRootImpl(String... segments) {
        return null;
    }

    public InputSupplier<InputStream> openImpl(ResourceType type, Identifier id) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return null;
        }

        try {
            final var supplier = Objects.requireNonNull(this.assets.get(id));
            return () -> new ByteArrayInputStream(Objects.requireNonNull(supplier.get()));
        } catch (Exception ignored) {
        }
        return null;
    }

    public void findResourcesImpl(ResourceType type, String namespace, String prefix, ResourcePack.ResultConsumer consumer) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return;
        }

        for (var id : this.assets.keySet()) {
            var supplier = this.assets.get(id);
            if (supplier == null) {
                continue;
            }
            InputSupplier<InputStream> inputSupplier = () -> new ByteArrayInputStream(supplier.get());
            if (id.getNamespace().equals(namespace) && id.getPath().startsWith(prefix)) {
                consumer.accept(id, inputSupplier);
            }
        }
    }

    public Set<String> getNamespacesImpl(ResourceType type) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return Set.of();
        }

        Set<String> namespaces = new HashSet<>();
        for (var id : this.assets.keySet()) {
            namespaces.add(id.getNamespace());
        }
        return namespaces;
    }

    public void closeImpl() {
        LOGGER.info("closing pack: {}", this.name);
    }

    public <T> T parseMetadataImpl(ResourceMetadataReader<T> metaReader) {
        try {
            var stream = Objects.requireNonNull(this.openRootImpl("pack.mcmeta")).get();
            return AbstractFileResourcePack.parseMetadata(metaReader, Objects.requireNonNull(stream));
        } catch (Exception ignored) {
            if (metaReader.getKey().equals("pack")) {
                JsonObject object = new JsonObject();
                object.addProperty("pack_format", this.packVersion);
                object.addProperty("description", "%s Runtime ResPack".formatted(ExtraSounds.class.getSimpleName()));
                return metaReader.fromJson(object);
            } else {
                return null;
            }
        }
    }
}
