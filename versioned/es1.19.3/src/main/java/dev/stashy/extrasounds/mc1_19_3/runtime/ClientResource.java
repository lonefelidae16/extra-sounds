package dev.stashy.extrasounds.mc1_19_3.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import net.minecraft.resource.InputSupplier;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class ClientResource extends VersionedClientResource implements ResourcePack {
    public ClientResource(String modId, String packName) {
        super(modId, packName);
        this.name = packName;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        return super.openRootImpl(segments);
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        return super.openImpl(type, id);
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        super.findResourcesImpl(type, namespace, prefix, consumer);
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
