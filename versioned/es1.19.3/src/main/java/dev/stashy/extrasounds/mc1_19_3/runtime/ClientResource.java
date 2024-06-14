package dev.stashy.extrasounds.mc1_19_3.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public class ClientResource extends VersionedClientResource implements ResourcePack {
    public ClientResource(String modId, String packName) {
        super(modId, packName);
        this.name = packName;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        return null;
    }

    @Override
    protected Supplier<InputStream> openRootImpl(String... segments) {
        try {
            var stream = Objects.requireNonNull(this.openRoot(segments)).get();
            return () -> Objects.requireNonNull(stream);
        } catch (Exception ignored) {
        }
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
        return super.getNamespacesImpl(type);
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) throws IOException {
        return super.parseMetadataImpl(metaReader);
    }

    @Override
    public String getName() {
        return this.name.toString();
    }

    @Override
    public void close() {
        super.closeImpl();
    }
}
