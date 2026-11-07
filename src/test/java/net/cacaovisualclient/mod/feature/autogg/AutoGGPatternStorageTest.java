package net.cacaovisualclient.mod.feature.autogg;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AutoGGPatternStorageTest {
    @TempDir
    Path directory;

    @Test
    void unreadableFileIsKeptUntilItCanBeReloaded() throws Exception {
        Path file = directory.resolve("autogg-patterns.json");
        Files.writeString(file, "{");
        AutoGGPatternStorage storage = new AutoGGPatternStorage(file.toFile());

        assertFalse(storage.saveSettings("Good game", 250));
        assertEquals("{", Files.readString(file));
        Files.writeString(file, "{\"version\":1,\"message\":\"gg\",\"delayMs\":500}");
        assertTrue(storage.reload());
        assertEquals("gg", storage.getConfig().getMessage());
        assertTrue(storage.saveSettings("Good game", 250));
        assertEquals(250, new AutoGGPatternStorage(file.toFile()).getConfig().getDelayMs());
    }

    @Test
    void failedWriteDoesNotChangeTheActiveConfig() throws Exception {
        Path file = directory.resolve("autogg-patterns.json");
        AutoGGPatternStorage storage = new AutoGGPatternStorage(file.toFile());
        Files.createDirectory(file);
        Files.writeString(file.resolve("keep.txt"), "keep");

        assertFalse(storage.saveSettings("Good game", 250));
        assertFalse(storage.addPattern(new AutoGGPattern("Round", "example.org", "Winner:", AutoGGMatchMode.CONTAINS)));
        assertEquals("GG", storage.getConfig().getMessage());
        assertEquals(1000, storage.getConfig().getDelayMs());
        assertTrue(storage.getPatterns().isEmpty());
    }
}
