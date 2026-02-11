package net.coralmod.mod.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

import java.util.List;

@Getter
public class ModeSetting extends Setting<String> {

    private final List<String> modes;

    public ModeSetting(String name, String defaultMode, List<String> modes) {
        super(name, defaultMode);
        this.modes = modes;
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void read(JsonElement json) {
        setValue(json.getAsString());
    }
}
