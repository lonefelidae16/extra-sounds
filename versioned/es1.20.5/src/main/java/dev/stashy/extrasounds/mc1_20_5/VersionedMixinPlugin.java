package dev.stashy.extrasounds.mc1_20_5;

import me.lonefelidae16.groominglib.api.AbstractVersionedMixinPlugin;

public final class VersionedMixinPlugin extends AbstractVersionedMixinPlugin {
    @Override
    protected CharSequence startVersion() {
        return SupportedVersions.START;
    }

    @Override
    protected CharSequence endVersion() {
        return SupportedVersions.END;
    }
}
