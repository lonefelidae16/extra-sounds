package dev.stashy.extrasounds.mc1_20_2;

import me.lonefelidae16.groominglib.api.AbstractVersionedMixinPlugin;

public final class VersionedMixinPlugin extends AbstractVersionedMixinPlugin {
    @Override
    protected String earlierVersion() {
        return "1.20.2";
    }

    @Override
    protected String laterVersion() {
        return "1.20.4";
    }
}
