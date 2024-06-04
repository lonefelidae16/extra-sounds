package dev.stashy.extrasounds.mc1_20;

import me.lonefelidae16.groominglib.api.AbstractVersionedMixinPlugin;

public final class VersionedMixinPlugin extends AbstractVersionedMixinPlugin {
    @Override
    protected String startVersion() {
        return "1.20";
    }

    @Override
    protected String endVersion() {
        return "1.20.1";
    }
}
