package dev.stashy.extrasounds.v1_20_3;

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
