package net.cacaovisualclient.mod.feature.autogg;

import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class AutoGGPatternStorage {

    private static final File FILE = new File("cacaovisualclient/autogg-patterns.json");

    private AutoGGConfig config;

    public AutoGGPatternStorage() {
        reload();
    }

    public void reload() {
        config = JsonUtils.loadFromJson(FILE, AutoGGConfig.class);
        if (config == null) {
            config = new AutoGGConfig();
        }

        config.normalize();
        save();
    }

    public void save() {
        config.normalize();
        JsonUtils.saveToJson(FILE, config);
    }

    public AutoGGConfig getConfig() {
        return config;
    }

    public List<AutoGGPattern> getPatterns() {
        return config.getPatterns();
    }

    public Optional<AutoGGPattern> getPattern(String name) {
        return config.getPatterns().stream()
                .filter(pattern -> pattern.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public boolean addPattern(AutoGGPattern pattern) {
        if (pattern == null || !pattern.isValid() || getPattern(pattern.getName()).isPresent()) {
            return false;
        }

        config.getPatterns().add(pattern);
        save();
        return true;
    }

    public boolean savePattern(AutoGGPattern pattern) {
        if (pattern == null || !pattern.isValid()) {
            return false;
        }

        final Optional<AutoGGPattern> existing = getPattern(pattern.getName());
        if (existing.isPresent()) {
            final AutoGGPattern existingPattern = existing.get();
            existingPattern.setServer(pattern.getServer().trim());
            existingPattern.setTrigger(pattern.getTrigger().trim());
            existingPattern.setMode(pattern.getMode());
            save();
            return true;
        }

        config.getPatterns().add(pattern);
        save();
        return true;
    }

    public boolean removePattern(String name) {
        final boolean removed = config.getPatterns()
                .removeIf(pattern -> pattern.getName().equalsIgnoreCase(name));

        if (removed) {
            save();
        }

        return removed;
    }

    public boolean setPatternEnabled(String name, boolean enabled) {
        final Optional<AutoGGPattern> pattern = getPattern(name);
        if (pattern.isEmpty()) {
            return false;
        }

        pattern.get().setEnabled(enabled);
        save();
        return true;
    }

    public void setMessage(String message) {
        config.setMessage(message);
        save();
    }

    public void setDelayMs(long delayMs) {
        config.setDelayMs(delayMs);
        save();
    }
}
