package net.cacaovisualclient.mod.config;

import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;

public class ConfigStorage {

    private static final File CONFIG_FILE = new File("cacaovisualclient/config.json");

    public Config load() {
        Config config = JsonUtils.loadFromJson(CONFIG_FILE, Config.class);

        if (config == null) {
            config = new Config();
            CONFIG_FILE.getParentFile().mkdirs();
            save(config);
        }

        if (config.getVersion() != Config.VERSION) {
            CacaoVisualClient.LOGGER.warn("Config version does not match the expected version! Expected {}, got {}", Config.VERSION, config.getVersion());
        }

        config.setVersion(Config.VERSION);
        save(config);
        return config;
    }

    public void save(Config config) {
        JsonUtils.saveToJson(CONFIG_FILE, config);
    }
}
