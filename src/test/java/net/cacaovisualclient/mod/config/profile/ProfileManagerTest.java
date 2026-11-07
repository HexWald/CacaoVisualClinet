package net.cacaovisualclient.mod.config.profile;

import net.cacaovisualclient.mod.config.Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileManagerTest {
    @TempDir
    Path directory;

    @Test
    void failedStartupLoadKeepsTheProfileFile() throws Exception {
        Path file = directory.resolve("Default.json");
        String broken = "{";
        Files.writeString(file, broken);
        ProfileManager manager = new ProfileManager(new Config(), new ProfileStorage(directory.toFile(), List.of()));

        assertNull(manager.getCurrentProfile());
        assertFalse(manager.saveCurrentProfile());
        assertEquals(broken, Files.readString(file));
    }

    @Test
    void renamedFileUsesItsNewName() throws Exception {
        Files.writeString(directory.resolve("Arena.json"), """
                {"version":1,"name":"Old name","modules":{}}
                """);
        Config config = new Config();
        ProfileManager manager = new ProfileManager(config, new ProfileStorage(directory.toFile(), List.of()));

        assertEquals("Arena", config.getCurrentProfile());
        assertTrue(manager.renameProfile("Arena", "Duels"));
        assertTrue(manager.load("Duels"));
        assertEquals("Duels", config.getCurrentProfile());
        assertFalse(Files.exists(directory.resolve("Old name.json")));
    }
}
