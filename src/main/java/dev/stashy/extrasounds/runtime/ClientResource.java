package dev.stashy.extrasounds.runtime;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonObject;
import dev.stashy.extrasounds.ExtraSounds;
import dev.stashy.extrasounds.impl.PrefixableMessageFactory;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientResource implements ResourcePack {
    private final int packVersion;
    private final CharSequence name;
    private final Map<Identifier, Supplier<byte[]>> assets;
    private final ResourcePackInfo info;

    private static final ExecutorService EXECUTOR_SERVICE;
    private static final Logger LOGGER;

    static {
        int proc = Math.max(Runtime.getRuntime().availableProcessors() / 2 - 1, 1);
        LOGGER = LogManager.getLogger(
                ClientResource.class,
                new PrefixableMessageFactory(
                        "%s/ResourcePack".formatted(ExtraSounds.class.getSimpleName())
                )
        );
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(proc, new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ResPack-Workers-%s").build());
    }

    public ClientResource(String modId, String packName) {
        this.packVersion = 5;
        this.assets = new ConcurrentHashMap<>();
        this.name = packName;
        this.info = new ResourcePackInfo(modId, Text.literal(packName), new ResourcePackSource() {
            @Override
            public Text decorate(Text packDisplayName) {
                return packDisplayName;
            }

            @Override
            public boolean canBeEnabledLater() {
                return false;
            }
        }, Optional.of(new VersionedIdentifier(Identifier.DEFAULT_NAMESPACE, modId, String.valueOf(this.packVersion))));
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        return null;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
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

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
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

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return Set.of();
        }

        Set<String> namespaces = new HashSet<>();
        for (var id : this.assets.keySet()) {
            namespaces.add(id.getNamespace());
        }
        return namespaces;
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        try {
            var stream = Objects.requireNonNull(this.openRoot("pack.mcmeta")).get();
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

    @Override
    public ResourcePackInfo getInfo() {
        return this.info;
    }

    @Override
    public void close() {
        LOGGER.info("closing pack: {}", this.name);
    }

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
}
