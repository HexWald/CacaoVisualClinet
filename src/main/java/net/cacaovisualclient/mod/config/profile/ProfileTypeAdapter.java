package net.cacaovisualclient.mod.config.profile;

import com.google.gson.*;
import lombok.RequiredArgsConstructor;
import net.cacaovisualclient.mod.module.HudModule;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.settings.Setting;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class ProfileTypeAdapter implements JsonSerializer<Profile>, JsonDeserializer<Profile> {

    private static final int VERSION = 1;

    private final Collection<Module> modules;

    @Override
    public JsonElement serialize(Profile src, Type typeOfSrc, JsonSerializationContext context) {
        final JsonObject root = new JsonObject();
        root.addProperty("version", VERSION);
        root.addProperty("name", src.getName());

        final JsonObject modulesObject = new JsonObject();

        for (Module module : modules) {
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

        if (!root.has("version") || root.get("version").getAsInt() != VERSION) {
            throw new JsonParseException("Unsupported profile version");
        }
        String name = root.get("name").getAsString();
        if (!ProfileNames.isValid(name)) {
            throw new JsonParseException("Invalid profile name");
        }

        final JsonObject modulesObject = root.getAsJsonObject("modules");
        if (modulesObject == null) {
            throw new JsonParseException("Missing profile modules");
        }

        final List<Runnable> changes = new ArrayList<>();
        final List<Runnable> toggles = new ArrayList<>();

        for (Module module : modules) {
            final JsonObject moduleObject = modulesObject.getAsJsonObject(module.getName());
            if (moduleObject == null) {
                changes.add(module::reset);
                toggles.add(() -> module.setEnabled(false));
                continue;
            }

            JsonPrimitive enabledValue = moduleObject.getAsJsonPrimitive("enabled");
            if (enabledValue == null || !enabledValue.isBoolean()) {
                throw new JsonParseException("Invalid enabled state for " + module.getName());
            }
            boolean enabled = enabledValue.getAsBoolean();
            toggles.add(() -> module.setEnabled(enabled));

            final JsonObject settingsObject = moduleObject.getAsJsonObject("settings");
            for (Setting<?> setting : module.getSettings()) {
                if (settingsObject != null && settingsObject.has(setting.getName())) {
                    changes.add(readSetting(setting, settingsObject.get(setting.getName())));
                } else {
                    changes.add(setting::reset);
                }
            }

            if (module instanceof HudModule hud) {
                if (moduleObject.has("hud")) {
                    final JsonObject hudObject = moduleObject.getAsJsonObject("hud");
                    int x = hudObject.get("x").getAsInt();
                    int y = hudObject.get("y").getAsInt();
                    changes.add(() -> hud.move(x, y));
                } else {
                    changes.add(hud::resetPosition);
                }
            }

        }

        changes.forEach(Runnable::run);
        toggles.forEach(Runnable::run);
        return new Profile(name);
    }

    private static <T> Runnable readSetting(Setting<T> setting, JsonElement json) {
        T value = setting.parse(json);
        return () -> setting.setValue(value);
    }
}
