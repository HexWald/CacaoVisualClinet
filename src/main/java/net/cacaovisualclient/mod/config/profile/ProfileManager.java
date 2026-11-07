package net.cacaovisualclient.mod.config.profile;

import lombok.Getter;
import net.cacaovisualclient.mod.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    public static final String DEFAULT_PROFILE_NAME = "Default";

    private final Config config;
    private final ProfileStorage storage;

    @Getter
    private Profile currentProfile;
    private final List<Profile> profiles;

    public ProfileManager(Config config, ProfileStorage storage) {
        this.config = config;
        this.storage = storage;

        this.profiles = new ArrayList<>(storage.loadProfiles());

        if (profiles.isEmpty()) {
            createProfile(DEFAULT_PROFILE_NAME);
            return;
        }

        Profile requested = getProfile(config.getCurrentProfile());
        if (requested != null && load(requested.getName())) {
            return;
        }
        loadFallback(requested);
    }

    private void loadFallback(Profile failed) {
        Profile fallback = getProfile(DEFAULT_PROFILE_NAME);
        if (fallback != null && fallback != failed && load(fallback.getName())) {
            return;
        }
        for (Profile profile : profiles) {
            if (profile != failed && profile != fallback && load(profile.getName())) {
                return;
            }
        }
    }

    public Profile getProfile(String name) {
        return profiles.stream()
                .filter(profile -> profile.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<Profile> getProfiles() {
        return List.copyOf(profiles);
    }

    public boolean createProfile(String name) {
        final String cleanName = name == null ? "" : name.trim();
        if (!ProfileNames.isValid(cleanName) || getProfile(cleanName) != null || storage.exists(cleanName)) {
            return false;
        }

        final Profile profile = new Profile(cleanName);
        if (!storage.save(profile)) {
            return false;
        }
        profiles.add(profile);
        setCurrentProfile(profile);
        return true;
    }

    public boolean duplicateProfile(String sourceName, String targetName) {
        final String cleanTarget = targetName == null ? "" : targetName.trim();
        if (getProfile(sourceName) == null || !ProfileNames.isValid(cleanTarget) || getProfile(cleanTarget) != null) {
            return false;
        }

        if (!storage.copy(sourceName, cleanTarget)) {
            return false;
        }

        profiles.add(new Profile(cleanTarget));
        return true;
    }

    public boolean renameProfile(String sourceName, String targetName) {
        final String cleanTarget = targetName == null ? "" : targetName.trim();
        final Profile source = getProfile(sourceName);
        if (source == null || !ProfileNames.isValid(cleanTarget) || getProfile(cleanTarget) != null) {
            return false;
        }

        if (!storage.rename(source.getName(), cleanTarget)) {
            return false;
        }

        profiles.remove(source);
        final Profile renamed = new Profile(cleanTarget);
        profiles.add(renamed);

        if (currentProfile != null && currentProfile.getName().equals(source.getName())) {
            currentProfile = renamed;
            config.setCurrentProfile(cleanTarget);
        }

        return true;
    }

    public boolean deleteProfile(String name) {
        final Profile profile = getProfile(name);
        if (profile == null || profiles.size() <= 1 || DEFAULT_PROFILE_NAME.equals(profile.getName())) {
            return false;
        }

        if (!storage.delete(profile.getName())) {
            return false;
        }

        profiles.remove(profile);

        if (currentProfile != null && currentProfile.getName().equals(profile.getName())) {
            currentProfile = null;
            loadFallback(null);
        }

        return true;
    }

    public boolean saveProfile(String name) {
        final Profile profile = getProfile(name);
        return profile != null && storage.save(profile);
    }

    public boolean saveCurrentProfile() {
        return currentProfile != null && storage.save(currentProfile);
    }

    public boolean load(String name) {
        final Profile profile = storage.load(name);
        if (profile == null) {
            return false;
        }

        setCurrentProfile(profile);
        return true;
    }

    private void setCurrentProfile(Profile profile) {
        currentProfile = profile;
        config.setCurrentProfile(profile.getName());
    }
}
