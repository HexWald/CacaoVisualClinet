package net.cacaovisualclient.mod.config.profile;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import net.cacaovisualclient.mod.module.Module;
import net.cacaovisualclient.mod.module.ModuleInfo;
import net.cacaovisualclient.mod.module.settings.NumberSetting;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileTypeAdapterTest {
    @Test
    void invalidProfileDoesNotChangeEarlierModules() {
        First first = new First();
        ProfileTypeAdapter adapter = new ProfileTypeAdapter(List.of(first, new Second()));

        assertThrows(JsonParseException.class, () -> adapter.deserialize(JsonParser.parseString("""
                {"version":1,"name":"Arena","modules":{
                    "First":{"enabled":true,"settings":{"Size":5}},
                    "Second":{"enabled":"yes"}
                }}
                """), Profile.class, null));

        assertFalse(first.isEnabled());
        assertEquals(2.0, first.size.getValue());
    }

    @Test
    void enableUsesTheLoadedSettings() {
        First first = new First();
        ProfileTypeAdapter adapter = new ProfileTypeAdapter(List.of(first));

        adapter.deserialize(JsonParser.parseString("""
                {"version":1,"name":"Arena","modules":{
                    "First":{"enabled":true,"settings":{"Size":5}}
                }}
                """), Profile.class, null);

        assertTrue(first.isEnabled());
        assertEquals(5.0, first.sizeOnEnable);
    }

    @Test
    void olderProfileUsesDefaultsForMissingEntries() {
        First first = new First();
        Second second = new Second();
        first.size.setValue(8.0);
        second.setEnabled(true);
        ProfileTypeAdapter adapter = new ProfileTypeAdapter(List.of(first, second));

        adapter.deserialize(JsonParser.parseString("""
                {"version":1,"name":"Old","modules":{"First":{"enabled":true}}}
                """), Profile.class, null);

        assertTrue(first.isEnabled());
        assertEquals(2.0, first.sizeOnEnable);
        assertFalse(second.isEnabled());
    }

    @ModuleInfo(name = "First", description = "")
    private static class First extends Module {
        private final NumberSetting size = new NumberSetting("Size", 2.0, 1.0, 10.0, 1.0);
        private double sizeOnEnable;

        private First() {
            addSettings(size);
        }

        @Override
        public void onEnable() {
            sizeOnEnable = size.getValue();
        }
    }

    @ModuleInfo(name = "Second", description = "")
    private static class Second extends Module {
    }
}
