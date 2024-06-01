package dev.stashy.extrasounds;

import dev.stashy.extrasounds.logics.ExtraSounds;
import net.fabricmc.api.ClientModInitializer;

public final class ExtraSoundsBootstrap implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExtraSounds.init();
    }
}
