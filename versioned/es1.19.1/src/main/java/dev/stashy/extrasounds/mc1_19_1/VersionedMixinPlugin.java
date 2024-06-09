package dev.stashy.extrasounds.mc1_19_1;

import me.lonefelidae16.groominglib.api.AbstractVersionedMixinPlugin;

public final class VersionedMixinPlugin extends AbstractVersionedMixinPlugin {
    @Override
    protected String earlierVersion() {
        return "1.19.1";
    }

    @Override
    protected String laterVersion() {
        return "1.19.2";
    }
}
