package net.cacaovisualclient.mod.config.profile;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.cacaovisualclient.mod.CacaoVisualClient;
import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleManager;
import net.cacaovisualclient.mod.module.settings.Setting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProfileTypeAdapter implements JsonSerializer<Profile>, JsonDeserializer<Profile> {

    private static final int VERSION = 1;

    private final ModuleManager moduleManager;

    @Override
    public JsonElement serialize(Profile src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject root = new JsonObject();
        root.addProperty("version", VERSION);
        root.addProperty("name", src.getName());

        final JsonObject modulesObject = new JsonObject();

        for (Module module : moduleManager.getModules()) {
            final JsonObject moduleObject = new JsonObject();
            moduleObject.addProperty("enabled", module.isEnabled());

            final JsonObject moduleSettings = new JsonObject();
            for (Setting<?> setting : module.getSettings()) {
                moduleSettings.add(setting.getName(), setting.write());
            }

            moduleObject.add("settings", moduleSettings);

            if (module instanceof HudModule hudModule) {
                final JsonObject hudObject = new JsonObject();
                hudObject.addProperty("x", hudModule.getX());
                hudObject.addProperty("y", (hudModule.getY()));
                moduleObject.add("hud", hudObject);
            }

            modulesObject.add(module.getName(), moduleObject);
        }

        root.add("modules", modulesObject);
        return root;
    }

    @Override
    public Profile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject root = json.getAsJsonObject();

        if (root.get("version").getAsInt() != VERSION) {
            CacaoVisualClient.LOGGER.warn("Profile version does not match the expected version! Expected {}, got {}", VERSION, root.get("version").getAsInt());
        }

        final JsonObject modulesObject = root.getAsJsonObject("modules");

        final List<Module> enabledModules = new ArrayList<>();

        for (Module module : moduleManager.getModules()) {
            final JsonObject moduleObject = modulesObject.getAsJsonObject(module.getName());
            if (moduleObject == null) {
                continue;
            }

            module.setEnabled(moduleObject.get("enabled").getAsBoolean());

            final JsonObject settingsObject = moduleObject.getAsJsonObject("settings");
            if (settingsObject != null) {
                for (Setting<?> setting : module.getSettings()) {
                    if (setting == null) {
                        continue;
                    }
                    try {
                        setting.read(settingsObject.get(setting.getName()));
                    } catch (Exception e) {
                        CacaoVisualClient.LOGGER.error("Failed to read setting {} for module {}", setting.getName(), module.getName(), e);
                    }
                }
            }

            if (module instanceof HudModule hud && moduleObject.has("hud")) {
                final JsonObject hudObject = moduleObject.getAsJsonObject("hud");
                hud.setX(hudObject.get("x").getAsInt());
                hud.setY(hudObject.get("y").getAsInt());
            }

            if (module.isEnabled()) {
                enabledModules.add(module);
            }
        }

        return new Profile(root.get("name").getAsString(), enabledModules);
    }
}
