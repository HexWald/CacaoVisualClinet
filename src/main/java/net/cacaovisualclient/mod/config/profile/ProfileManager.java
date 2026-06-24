package net.cacaovisualclient.mod.config.profile;

import lombok.Getter;
import net.cacaovisualclient.mod.config.Config;
import net.cacaovisualclient.mod.config.ConfigStorage;
import net.cacaovisualclient.mod.module.ModuleManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    public static final String DEFAULT_PROFILE_NAME = "Default";

    private final Config config;
    private final ConfigStorage configStorage;
    private final ProfileStorage storage;
    private final ModuleManager moduleManager;

    @Getter
    private Profile currentProfile;
    private final List<Profile> profiles;

    public ProfileManager(Config config, ConfigStorage configStorage, ProfileStorage storage, ModuleManager moduleManager) {
        this.config = config;
        this.configStorage = configStorage;
        this.storage = storage;
        this.moduleManager = moduleManager;

        this.profiles = new ArrayList<>(storage.loadProfiles());

        if (profiles.isEmpty()) {
            // Create default profile if no profiles exist
            createProfile(DEFAULT_PROFILE_NAME);
        }

        load(config.getCurrentProfile());
    }

    public Profile getProfile(String name) {
        return profiles.stream()
                .filter(profile -> profile.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public void createProfile(String name) {
        final Profile profile = new Profile(name, moduleManager.getEnabledModules());
        storage.save(profile);
        profiles.add(profile);
    }

    public void saveProfile(Profile profile) {
        storage.save(profile);
    }

    public void saveProfile(String name) {
        final Profile profile = getProfile(name);
        if (profile == null) {
            return;
        }
        saveProfile(profile);
    }

    public void saveCurrentProfile() {
        saveProfile(currentProfile);
    }

    public void load(String name) {
        final Profile profile = storage.load(name);
        if (profile == null) {
            return;
        }

        setCurrentProfile(profile);
    }

    public void setCurrentProfile(Profile profile) {
        currentProfile = profile;
        config.setCurrentProfile(profile.getName());
        saveCurrentProfile();
    }

    public void setCurrentProfile(String name) {
        final Profile profile = getProfile(name);
        if (profile == null) {
            return;
        }

        setCurrentProfile(profile);
    }
}
