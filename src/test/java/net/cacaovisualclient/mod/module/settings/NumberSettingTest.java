package net.cacaovisualclient.mod.module.settings;

import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumberSettingTest {

    private final NumberSetting scale = new NumberSetting("Scale", 100.0, 50.0, 200.0, 5.0);

    @Test
    void clampsValuesLoadedFromProfiles() {
        scale.read(new JsonPrimitive(-10.0));
        assertEquals(50.0, scale.getValue());

        scale.read(new JsonPrimitive(10_000.0));
        assertEquals(200.0, scale.getValue());
    }

    @Test
    void keepsValidValues() {
        scale.read(new JsonPrimitive(125.0));
        assertEquals(125.0, scale.getValue());
        assertEquals(125.0, scale.write().getAsDouble());
    }

    @Test
    void rejectsNonFiniteValuesWithoutChangingTheSetting() {
        for (double value : new double[]{Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> scale.read(new JsonPrimitive(value)));
            assertEquals(100.0, scale.getValue());
        }
        assertThrows(IllegalArgumentException.class, () -> scale.setValue(null));
    }

    @Test
    void notifiesListenersOnlyWhenTheClampedValueChanges() {
        List<Double> changes = new ArrayList<>();
        scale.onChange((oldValue, value) -> changes.add(value));

        scale.setValue(500.0);
        scale.setValue(600.0);

        assertEquals(List.of(200.0), changes);
    }
}
