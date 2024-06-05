package dev.stashy.extrasounds.mc1_19_2.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ClientResource extends VersionedClientResource implements ResourcePack {
    public ClientResource(String modId, String packName) {
        super(modId, packName);
        this.name = packName;
    }

    @Nullable
    @Override
    public InputStream openRoot(String fileName) {
        return null;
    }

    @Override
    protected Supplier<InputStream> openRootImpl(String... segments) {
        return () -> this.openRoot(String.join(File.separator, segments));
    }

    @Override
    public InputStream open(ResourceType type, Identifier id) throws IOException {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return null;
        }

        try {
            final var supplier = Objects.requireNonNull(this.assets.get(id));
            return new ByteArrayInputStream(Objects.requireNonNull(supplier.get()));
        } catch (Exception ignored) {
        }
        return null;
    }

    @Override
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, Predicate<Identifier> allowedPathPredicate) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return List.of();
        }

        List<Identifier> result = Lists.newArrayList();
        for (var id : this.assets.keySet()) {
            var supplier = this.assets.get(id);
            if (supplier == null) {
                continue;
            }
            if (id.getNamespace().equals(namespace) && id.getPath().startsWith(prefix) && allowedPathPredicate.test(id)) {
                result.add(id);
            }
        }
        return result;
    }

    @Override
    public boolean contains(ResourceType type, Identifier id) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return false;
        }
        for (var key : this.assets.keySet()) {
            var supplier = this.assets.get(key);
            if (supplier == null) {
                continue;
            }
            if (key.equals(id)) {
                return true;
            }
        }
        return false;
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
