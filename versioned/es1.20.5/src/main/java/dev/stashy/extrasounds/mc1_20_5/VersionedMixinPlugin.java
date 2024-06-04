package dev.stashy.extrasounds.mc1_20_5;

import me.lonefelidae16.groominglib.api.AbstractVersionedMixinPlugin;

public final class VersionedMixinPlugin extends AbstractVersionedMixinPlugin {
    @Override
    protected String startVersion() {
        return "1.20.5";
    }

    @Override
    protected String endVersion() {
        return "1.20.6";
    }
}
