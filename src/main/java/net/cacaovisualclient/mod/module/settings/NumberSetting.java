package net.cacaovisualclient.mod.module.settings;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Getter;

@Getter
public class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(String name, double defaultValue, double min, double max, double increment) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    @Override
    public JsonElement write() {
        return new JsonPrimitive(getValue());
    }

    @Override
    public void read(JsonElement json) {
        setValue(json.getAsDouble());
    }
}
