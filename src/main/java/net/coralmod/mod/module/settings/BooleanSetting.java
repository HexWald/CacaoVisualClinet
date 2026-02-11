package net.coralmod.mod.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void read(JsonElement json) {
        setValue(json.getAsBoolean());
    }
}
