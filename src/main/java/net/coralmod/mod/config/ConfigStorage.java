package net.coralmod.mod.config;

import net.coralmod.mod.CoralMod;
import net.coralmod.mod.utils.JsonUtils;

import java.io.File;

public class ConfigStorage {

    private static final File CONFIG_FILE = new File("coralmod/config.json");

    public Config load() {
        Config config = JsonUtils.loadFromJson(CONFIG_FILE, Config.class);

        if (config == null) {
            config = new Config();
            CONFIG_FILE.getParentFile().mkdirs();
            save(config);
        }

        if (config.getVersion() != Config.VERSION) {
            CoralMod.LOGGER.warn("Config version does not match the expected version! Expected {}, got {}", Config.VERSION, config.getVersion());
        }

        config.setVersion(Config.VERSION);
        save(config);
        return config;
    }

    public void save(Config config) {
        JsonUtils.saveToJson(CONFIG_FILE, config);
    }
}
