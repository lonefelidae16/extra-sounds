package dev.stashy.extrasounds.mc1_20_5.runtime;

import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.util.Optional;
import java.util.Set;

public class ClientResource extends VersionedClientResource implements ResourcePack {
    private final ResourcePackInfo info;

    public ClientResource(String modId, String packName) {
        super(modId, packName);
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

    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        return super.openRootImpl(segments);
    }

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

    @Override
    public void close() {
        super.closeImpl();
    }

    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        return super.parseMetadataImpl(metaReader);
    }

    @Override
    public ResourcePackInfo getInfo() {
        return this.info;
    }
}
