package net.cacaovisualclient.mod.config.profile;

import lombok.Getter;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.config.Config;
import net.cacaovisualclient.mod.module.ModuleManager;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    public static final String DEFAULT_PROFILE_NAME = "Default";

    private final Config config;
    private final ProfileStorage storage;
    private final ModuleManager moduleManager;

    @Getter
    private Profile currentProfile;
    private final List<Profile> profiles;

    public ProfileManager(Config config, ProfileStorage storage, ModuleManager moduleManager) {
        this.config = config;
        this.storage = storage;
        this.moduleManager = moduleManager;

        this.profiles = new ArrayList<>(storage.loadProfiles());

        if (profiles.isEmpty()) {
            // Create default profile if no profiles exist
            createProfile(DEFAULT_PROFILE_NAME);
        }

        final Profile requestedProfile = getProfile(config.getCurrentProfile());
        final Profile fallbackProfile = getProfile(DEFAULT_PROFILE_NAME);
        final Profile profileToLoad = requestedProfile != null
                ? requestedProfile
                : fallbackProfile != null ? fallbackProfile : profiles.getFirst();

        if (requestedProfile == null) {
            CacaoVisualClient.LOGGER.warn(
                    "Profile '{}' was not found, falling back to '{}'",
                    config.getCurrentProfile(),
                    profileToLoad.getName()
            );
        }

        if (!load(profileToLoad.getName())) {
            CacaoVisualClient.LOGGER.warn("Failed to load profile '{}', using current module defaults", profileToLoad.getName());
            setCurrentProfile(profileToLoad);
        }
    }

    public Profile getProfile(String name) {
        return profiles.stream()
                .filter(profile -> profile.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<Profile> getProfiles() {
        return List.copyOf(profiles);
    }

    public boolean createProfile(String name) {
        final String cleanName = name == null ? "" : name.trim();
        if (cleanName.isBlank() || getProfile(cleanName) != null) {
            return false;
        }

        final Profile profile = new Profile(cleanName, moduleManager.getEnabledModules());
        storage.save(profile);
        profiles.add(profile);
        setCurrentProfile(profile);
        return true;
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
        if (currentProfile != null) {
            saveProfile(currentProfile);
        }
    }

    public boolean load(String name) {
        final Profile profile = storage.load(name);
        if (profile == null) {
            return false;
        }

        setCurrentProfile(profile);
        return true;
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
