package net.cacaovisualclient.mod.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilsTest {

    @TempDir
    Path directory;

    @Test
    void replacesAnExistingFileWithCompleteJson() throws Exception {
        Path file = directory.resolve("config.json");
        Files.writeString(file, "old content");

        assertTrue(JsonUtils.saveToJson(file.toFile(), Map.of("theme", "CACAO")));

        JsonObject saved = new Gson().fromJson(Files.readString(file), JsonObject.class);
        assertEquals("CACAO", saved.get("theme").getAsString());
        assertOnlyConfigRemains(file);
    }

    @Test
    void keepsTheOriginalFileWhenSerializationFails() throws Exception {
        Path file = directory.resolve("config.json");
        String original = "{\"theme\":\"CACAO\"}";
        Files.writeString(file, original);
        Gson brokenSerializer = new GsonBuilder()
                .registerTypeAdapter(BrokenValue.class, (JsonSerializer<BrokenValue>) (value, type, context) -> {
                    throw new IllegalStateException("Cannot serialize this value");
                })
                .create();

        assertFalse(JsonUtils.saveToJson(brokenSerializer, file.toFile(), new BrokenValue()));

        assertEquals(original, Files.readString(file));
        assertOnlyConfigRemains(file);
    }

    @Test
    void createsMissingParentDirectories() {
        Path file = directory.resolve("profiles/new.json");

        assertTrue(JsonUtils.saveToJson(file.toFile(), Map.of("name", "New")));
        assertEquals("New", JsonUtils.loadFromJson(file.toFile(), JsonObject.class).get("name").getAsString());
    }

    @Test
    void reportsFailureWhenTheDestinationIsADirectory() throws Exception {
        Path destination = Files.createDirectory(directory.resolve("config.json"));
        Path existing = destination.resolve("keep.txt");
        Files.writeString(existing, "keep");

        assertFalse(JsonUtils.saveToJson(destination.toFile(), Map.of("theme", "CACAO")));

        assertEquals("keep", Files.readString(existing));
        assertOnlyConfigRemains(destination);
    }

    private void assertOnlyConfigRemains(Path file) throws Exception {
        try (var files = Files.list(directory)) {
            assertEquals(List.of(file), files.toList());
        }
    }

    private record BrokenValue() {
    }
}
