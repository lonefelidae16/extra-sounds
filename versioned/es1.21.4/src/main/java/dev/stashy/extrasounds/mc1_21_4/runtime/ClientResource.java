package dev.stashy.extrasounds.mc1_21_4.runtime;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import dev.stashy.extrasounds.logics.ExtraSounds;
import dev.stashy.extrasounds.logics.runtime.VersionedClientResource;
import net.minecraft.registry.VersionedIdentifier;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataSerializer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

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

    @Override
    public @Nullable <T> T parseMetadata(ResourceMetadataSerializer<T> metaReader) throws IOException {
        try {
            var stream = Objects.requireNonNull(this.openRootImpl("pack.mcmeta")).get();
            return AbstractFileResourcePack.parseMetadata(metaReader, Objects.requireNonNull(stream));
        } catch (Exception ignored) {
            if (metaReader.name().equals("pack")) {
                final JsonObject object = super.createPackJson();
                return metaReader.codec().parse(JsonOps.INSTANCE, object).ifError(tError -> ExtraSounds.LOGGER.error("Cannot register Runtime ResPack: {}", tError)).result().orElse(null);
            } else {
                return null;
            }
        }
    }

    @Override
    public void close() {
        super.closeImpl();
    }

    @Override
    public ResourcePackInfo getInfo() {
        return this.info;
    }
}
