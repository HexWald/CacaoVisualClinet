package net.cacaovisualclient.mod.module.settings;

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
    public Boolean parse(JsonElement json) {
        if (!json.getAsJsonPrimitive().isBoolean()) {
            throw new IllegalArgumentException(getName() + " must be true or false");
        }
        return json.getAsBoolean();
    }
}
