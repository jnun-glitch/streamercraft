package dev.streamercraft.config;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Properties;

/** Small, dependency-free settings store for the initial client foundation. */
public final class SettingsConfig {
    public static final String COSMIC_THEME = "cosmic";
    public static final String MIDNIGHT_THEME = "midnight";

    private static final String THEME_KEY = "theme";

    private final Path file;
    private final Logger logger;
    private String themeId;

    private SettingsConfig(Path file, Logger logger, String themeId) {
        this.file = file;
        this.logger = logger;
        this.themeId = themeId;
    }

    public static SettingsConfig load(Path file, Logger logger) {
        Properties properties = new Properties();

        if (Files.isRegularFile(file)) {
            try (InputStream input = Files.newInputStream(file)) {
                properties.load(input);
            } catch (IOException | IllegalArgumentException exception) {
                logger.warn("Could not read StreamerCraft settings at {}; using defaults.", file, exception);
            }
        }

        String configuredTheme = properties.getProperty(THEME_KEY, COSMIC_THEME)
                .trim()
                .toLowerCase(Locale.ROOT);
        String theme = switch (configuredTheme) {
            case COSMIC_THEME, MIDNIGHT_THEME -> configuredTheme;
            default -> {
                logger.warn("Unknown StreamerCraft theme '{}'; using '{}'.", configuredTheme, COSMIC_THEME);
                yield COSMIC_THEME;
            }
        };

        return new SettingsConfig(file, logger, theme);
    }

    public String themeId() {
        return themeId;
    }

    public void toggleTheme() {
        themeId = COSMIC_THEME.equals(themeId) ? MIDNIGHT_THEME : COSMIC_THEME;
        save();
    }

    private void save() {
        Path temporaryFile = file.resolveSibling(file.getFileName() + ".tmp");
        Properties properties = new Properties();
        properties.setProperty(THEME_KEY, themeId);

        try {
            Files.createDirectories(file.getParent());
            try (OutputStream output = Files.newOutputStream(temporaryFile)) {
                properties.store(output, "StreamerCraft local settings");
            }

            try {
                Files.move(temporaryFile, file,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, file, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            logger.error("Could not save StreamerCraft settings to {}.", file, exception);
            try {
                Files.deleteIfExists(temporaryFile);
            } catch (IOException cleanupException) {
                logger.debug("Could not remove temporary settings file {}.", temporaryFile, cleanupException);
            }
        }
    }
}
