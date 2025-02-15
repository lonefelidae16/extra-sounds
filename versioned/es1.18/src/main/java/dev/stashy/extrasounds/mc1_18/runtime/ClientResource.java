package dev.stashy.extrasounds.mc1_18.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import net.minecraft.resource.AbstractFileResourcePack;
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
    public Collection<Identifier> findResources(ResourceType type, String namespace, String prefix, int maxDepth, Predicate<String> pathFilter) {
        if (type != ResourceType.CLIENT_RESOURCES) {
            return List.of();
        }

        List<Identifier> result = Lists.newArrayList();
        for (var id : this.assets.keySet()) {
            var supplier = this.assets.get(id);
            if (supplier == null) {
                continue;
            }
            if (id.getNamespace().equals(namespace) && id.getPath().startsWith(prefix)) {
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
        try {
            var stream = Objects.requireNonNull(this.openRootImpl("pack.mcmeta")).get();
            return AbstractFileResourcePack.parseMetadata(metaReader, Objects.requireNonNull(stream));
        } catch (Exception ignored) {
            if (metaReader.getKey().equals("pack")) {
                return metaReader.fromJson(super.createPackJson());
            } else {
                return null;
            }
        }
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
