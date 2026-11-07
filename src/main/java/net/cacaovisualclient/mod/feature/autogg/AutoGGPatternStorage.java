package net.cacaovisualclient.mod.feature.autogg;

import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AutoGGPatternStorage {

    private final File file;
    private AutoGGConfig config = new AutoGGConfig();
    private boolean unreadable;

    public AutoGGPatternStorage() {
        this(new File("cacaovisualclient/autogg-patterns.json"));
    }

    AutoGGPatternStorage(File file) {
        this.file = file;
        reload();
    }

    public boolean reload() {
        AutoGGConfig loaded = file.exists() ? JsonUtils.loadFromJson(file, AutoGGConfig.class) : new AutoGGConfig();
        unreadable = loaded == null || loaded.getVersion() != AutoGGConfig.VERSION;
        if (unreadable) {
            return false;
        }
        loaded.normalize();
        config = loaded;
        return true;
    }

    private boolean save(AutoGGConfig updated) {
        if (unreadable) {
            return false;
        }
        updated.normalize();
        if (!JsonUtils.saveToJson(file, updated)) {
            return false;
        }
        config = updated;
        return true;
    }

    private AutoGGConfig copyConfig() {
        AutoGGConfig copy = new AutoGGConfig();
        copy.setMessage(config.getMessage());
        copy.setDelayMs(config.getDelayMs());
        copy.setPatterns(new ArrayList<>(config.getPatterns()));
        return copy;
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

        AutoGGConfig updated = copyConfig();
        updated.getPatterns().add(pattern);
        return save(updated);
    }

    public boolean savePattern(AutoGGPattern pattern) {
        if (pattern == null || !pattern.isValid()) {
            return false;
        }

        return getPattern(pattern.getName())
                .map(existing -> replacePattern(existing, pattern, existing.isEnabled()))
                .orElseGet(() -> addPattern(pattern));
    }

    public boolean removePattern(String name) {
        AutoGGConfig updated = copyConfig();
        return updated.getPatterns().removeIf(pattern -> pattern.getName().equalsIgnoreCase(name)) && save(updated);
    }

    public boolean setPatternEnabled(String name, boolean enabled) {
        return getPattern(name).map(pattern -> replacePattern(pattern, pattern, enabled)).orElse(false);
    }

    private boolean replacePattern(AutoGGPattern existing, AutoGGPattern pattern, boolean enabled) {
        AutoGGPattern replacement = new AutoGGPattern(existing.getName(), pattern.getServer(),
                pattern.getTrigger(), AutoGGMatchMode.fromString(pattern.getMode()));
        replacement.setEnabled(enabled);
        AutoGGConfig updated = copyConfig();
        updated.getPatterns().set(updated.getPatterns().indexOf(existing), replacement);
        return save(updated);
    }

    public boolean saveSettings(String message, long delayMs) {
        AutoGGConfig updated = copyConfig();
        updated.setMessage(message);
        updated.setDelayMs(delayMs);
        return save(updated);
    }
}
