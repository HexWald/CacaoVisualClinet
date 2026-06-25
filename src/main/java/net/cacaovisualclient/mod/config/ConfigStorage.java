package net.cacaovisualclient.mod.config;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

public class ConfigStorage {

    private static final File CONFIG_FILE = new File("cacaovisualclient/config.json");

    public Config load() {
        Config config = JsonUtils.loadFromJson(CONFIG_FILE, Config.class);

        if (config == null) {
            backupBrokenConfig();
            config = new Config();
            save(config);
        }

        if (config.getVersion() != Config.VERSION) {
            CacaoVisualClient.LOGGER.warn("Config version does not match the expected version! Expected {}, got {}", Config.VERSION, config.getVersion());
        }

        if (config.getCurrentProfile() == null || config.getCurrentProfile().isBlank()) {
            config.setCurrentProfile("Default");
        }
        if (config.getSelectedTheme() == null || config.getSelectedTheme().isBlank()) {
            config.setSelectedTheme("TUBE");
        }
        if (config.getDiscordApplicationId() == null) {
            config.setDiscordApplicationId("");
        }

        config.setVersion(Config.VERSION);
        save(config);
        return config;
    }

    public void save(Config config) {
        JsonUtils.saveToJson(CONFIG_FILE, config);
    }

    private void backupBrokenConfig() {
        if (!CONFIG_FILE.isFile()) {
            return;
        }

        final File backup = new File(
                CONFIG_FILE.getParentFile(),
                CONFIG_FILE.getName() + ".broken-" + Instant.now().toEpochMilli()
        );

        try {
            Files.move(CONFIG_FILE.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            CacaoVisualClient.LOGGER.warn("Moved unreadable config to {}", backup.getAbsolutePath());
        } catch (IOException e) {
            CacaoVisualClient.LOGGER.error("Failed to back up unreadable config", e);
        }
    }
}
