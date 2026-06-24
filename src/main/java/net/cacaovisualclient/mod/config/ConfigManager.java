package net.cacaovisualclient.mod.config;

import lombok.Getter;

public class ConfigManager {

    private final ConfigStorage storage;

    @Getter
    private final Config config;

    public ConfigManager(ConfigStorage storage) {
        this.storage = storage;
        this.config = storage.load();
    }

    public void save() {
        storage.save(config);
    }
}
