package dev.stashy.extrasounds.mc1_20;

import dev.stashy.extrasounds.logics.VersionedMain;
import net.minecraft.util.Identifier;

public final class Main extends VersionedMain {
    @Override
    public Identifier generateIdentifier(String namespace, String path) {
        return Identifier.of(namespace, path);
    }
}
