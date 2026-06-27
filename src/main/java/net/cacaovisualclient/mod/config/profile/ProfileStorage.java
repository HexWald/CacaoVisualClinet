package net.cacaovisualclient.mod.config.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.ModuleManager;
import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProfileStorage {

    private static final File PROFILE_DIR = new File("cacaovisualclient/profiles");

    private final Gson gson;

    public ProfileStorage(ModuleManager moduleManager) {
        this.gson = new GsonBuilder().registerTypeAdapter(Profile.class, new ProfileTypeAdapter(moduleManager)).setPrettyPrinting().create();

        PROFILE_DIR.mkdirs();
    }

    public List<Profile> loadProfiles() {
        final List<Profile> profileList = new ArrayList<>();
        final File[] files = PROFILE_DIR.listFiles(file ->
                file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".json")
        );

        if (files == null) {
            CacaoVisualClient.LOGGER.warn("Failed to list profiles in {}", PROFILE_DIR.getAbsolutePath());
            return profileList;
        }

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        for (File file : files) {
            final Profile profile = loadMetadata(file);
            if (profile != null) {
                profileList.add(profile);
            }
        }

        return profileList;
    }

    public void save(Profile profile) {
        JsonUtils.saveToJson(gson, new File(PROFILE_DIR, profile.getName() + ".json"), profile);
    }

    public Profile load(String name) {
        return JsonUtils.loadFromJson(gson, new File(PROFILE_DIR, name + ".json"), Profile.class);
    }

    public boolean delete(String name) {
        return new File(PROFILE_DIR, name + ".json").delete();
    }

    public boolean copy(String sourceName, String targetName) {
        final File source = new File(PROFILE_DIR, sourceName + ".json");
        final File target = new File(PROFILE_DIR, targetName + ".json");

        if (!source.isFile() || target.exists()) {
            return false;
        }

        try (Reader reader = Files.newBufferedReader(source.toPath(), StandardCharsets.UTF_8)) {
            final JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            root.addProperty("name", targetName);
            JsonUtils.saveToJson(target, root);
            return true;
        } catch (IOException | RuntimeException e) {
            CacaoVisualClient.LOGGER.error("Failed to copy profile {} to {}", sourceName, targetName, e);
            return false;
        }
    }

    public boolean rename(String sourceName, String targetName) {
        if (!copy(sourceName, targetName)) {
            return false;
        }

        if (!delete(sourceName)) {
            CacaoVisualClient.LOGGER.warn("Copied profile '{}' to '{}', but failed to remove the old file", sourceName, targetName);
        }

        return true;
    }

    private Profile loadMetadata(File file) {
        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            final JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            final String fallbackName = file.getName().substring(0, file.getName().length() - 5);
            final String name = root.has("name") ? root.get("name").getAsString() : fallbackName;

            if (name.isBlank()) {
                throw new IllegalStateException("Profile name is empty");
            }

            return new Profile(name, List.of());
        } catch (IOException | RuntimeException e) {
            CacaoVisualClient.LOGGER.error("Skipping unreadable profile: {}", file.getName(), e);
            return null;
        }
    }
}
