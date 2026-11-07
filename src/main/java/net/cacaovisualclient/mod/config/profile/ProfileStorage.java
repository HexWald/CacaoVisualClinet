package net.cacaovisualclient.mod.config.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleManager;
import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ProfileStorage {

    private final File directory;
    private final Gson gson;

    public ProfileStorage(ModuleManager moduleManager) {
        this(new File("cacaovisualclient/profiles"), moduleManager.getModules());
    }

    ProfileStorage(File directory, Collection<Module> modules) {
        this.directory = directory;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Profile.class, new ProfileTypeAdapter(modules))
                .setPrettyPrinting()
                .create();

        directory.mkdirs();
    }

    public List<Profile> loadProfiles() {
        final List<Profile> profileList = new ArrayList<>();
        final File[] files = directory.listFiles(file ->
                file.isFile() && file.getName().endsWith(".json")
        );

        if (files == null) {
            CacaoVisualClient.LOGGER.warn("Failed to list profiles in {}", directory.getAbsolutePath());
            return profileList;
        }

        Arrays.sort(files, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));

        for (File file : files) {
            String name = file.getName().substring(0, file.getName().length() - 5);
            if (ProfileNames.isValid(name)) {
                profileList.add(new Profile(name));
            }
        }

        return profileList;
    }

    public boolean save(Profile profile) {
        if (!ProfileNames.isValid(profile.getName())) {
            CacaoVisualClient.LOGGER.warn("Cannot save profile with an invalid file name: {}", profile.getName());
            return false;
        }
        return JsonUtils.saveToJson(gson, file(profile.getName()), profile);
    }

    public Profile load(String name) {
        if (!ProfileNames.isValid(name)) {
            return null;
        }
        JsonObject root = JsonUtils.loadFromJson(file(name), JsonObject.class);
        if (root == null) {
            return null;
        }
        root.addProperty("name", name);
        try {
            return gson.fromJson(root, Profile.class);
        } catch (RuntimeException e) {
            CacaoVisualClient.LOGGER.error("Failed to load profile {}", name, e);
            return null;
        }
    }

    public boolean delete(String name) {
        return ProfileNames.isValid(name) && file(name).delete();
    }

    boolean exists(String name) {
        return file(name).exists();
    }

    public boolean copy(String sourceName, String targetName) {
        if (!ProfileNames.isValid(sourceName) || !ProfileNames.isValid(targetName)) {
            return false;
        }
        final File source = file(sourceName);
        final File target = file(targetName);

        if (!source.isFile() || target.exists()) {
            return false;
        }

        try (Reader reader = Files.newBufferedReader(source.toPath(), StandardCharsets.UTF_8)) {
            final JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            root.addProperty("name", targetName);
            return JsonUtils.saveToJson(target, root);
        } catch (IOException | RuntimeException e) {
            CacaoVisualClient.LOGGER.error("Failed to copy profile {} to {}", sourceName, targetName, e);
            return false;
        }
    }

    public boolean rename(String sourceName, String targetName) {
        if (!ProfileNames.isValid(sourceName) || !ProfileNames.isValid(targetName)) {
            return false;
        }
        try {
            Files.move(file(sourceName).toPath(), file(targetName).toPath());
            return true;
        } catch (IOException e) {
            CacaoVisualClient.LOGGER.error("Failed to rename profile {}", sourceName, e);
            return false;
        }
    }

    private File file(String name) {
        return new File(directory, name + ".json");
    }
}
