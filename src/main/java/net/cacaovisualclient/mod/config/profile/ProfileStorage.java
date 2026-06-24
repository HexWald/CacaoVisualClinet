package net.cacaovisualclient.mod.config.profile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.cacaovisualclient.mod.module.ModuleManager;
import net.cacaovisualclient.mod.utils.JsonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileStorage {

    private static final File PROFILE_DIR = new File("cacaovisualclient/profiles");

    private final Gson gson;

    public ProfileStorage(ModuleManager moduleManager) {
        this.gson = new GsonBuilder().registerTypeAdapter(Profile.class, new ProfileTypeAdapter(moduleManager)).setPrettyPrinting().create();

        PROFILE_DIR.mkdirs();
    }

    public List<Profile> loadProfiles() {
        final List<Profile> profileList = new ArrayList<>();

        for (File file : PROFILE_DIR.listFiles()) {
            if (!file.isFile() && !file.getName().endsWith(".json")) {
                continue;
            }

            final Profile profile = load(file.getName().substring(0, file.getName().length() - 5));
            profileList.add(profile);
        }

        return profileList;
    }

    public void save(Profile profile) {
        JsonUtils.saveToJson(gson, new File(PROFILE_DIR, profile.getName() + ".json"), profile);
    }

    public Profile load(String name) {
        return JsonUtils.loadFromJson(gson, new File(PROFILE_DIR, name + ".json"), Profile.class);
    }
}
