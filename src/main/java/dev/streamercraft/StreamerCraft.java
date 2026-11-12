package dev.streamercraft;

import dev.streamercraft.config.SettingsConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public final class StreamerCraft implements ModInitializer {
    public static final String MOD_ID = "streamercraft";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static SettingsConfig settings;

    @Override
    public void onInitialize() {
        Path settingsPath = FabricLoader.getInstance()
                .getConfigDir()
                .resolve(MOD_ID)
                .resolve("settings.properties");
        settings = SettingsConfig.load(settingsPath, LOGGER);
        LOGGER.info("StreamerCraft {} initialized for Minecraft {}.",
                FabricLoader.getInstance().getModContainer(MOD_ID)
                        .map(container -> container.getMetadata().getVersion().getFriendlyString())
                        .orElse("unknown"),
                "1.21.11");
    }

    public static SettingsConfig settings() {
        if (settings == null) {
            throw new IllegalStateException("StreamerCraft settings were accessed before initialization.");
        }
        return settings;
    }
}
