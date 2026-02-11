package net.coralmod.mod.module.settings;

import com.google.gson.JsonElement;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Getter
public abstract class Setting<T> {

    private final String name;

    private T value;
    private final T defaultValue;

    private final List<BiConsumer<T, T>> changeListeners = new ArrayList<>();

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public void setValue(T value) {
        final T oldValue = this.value;

        if (oldValue.equals(value)) {
            return;
        }

        this.value = value;

        for (BiConsumer<T, T> listener : changeListeners) {
            listener.accept(oldValue, value);
        }
    }

    public void onChange(BiConsumer<T, T> listener) {
        changeListeners.add(listener);
    }

    public void reset() {
        value = defaultValue;
    }

    public abstract JsonElement write();

    public abstract void read(JsonElement json);

}
