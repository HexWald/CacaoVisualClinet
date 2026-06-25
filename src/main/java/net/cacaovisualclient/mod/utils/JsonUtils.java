package net.cacaovisualclient.mod.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import net.cacaovisualclient.mod.CacaoVisualClient;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@UtilityClass
public class JsonUtils {

    private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

    public <T> T loadFromJson(File file, Class<T> clazz) {
        return loadFromJson(DEFAULT_GSON, file, clazz);
    }

    public <T> T loadFromJson(Gson gson, File file, Class<T> clazz) {
        if (!file.isFile()) {
            return null;
        }

        try (Reader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException | RuntimeException e) {
            CacaoVisualClient.LOGGER.error("Failed to load json from file: {}", file.getName(), e);
        }
        return null;
    }

    public void saveToJson(File file, Object object) {
        saveToJson(DEFAULT_GSON, file, object);
    }

    public void saveToJson(Gson gson, File file, Object object) {
        final File parent = file.getParentFile();

        try {
            if (parent != null) {
                Files.createDirectories(parent.toPath());
            }
        } catch (IOException e) {
            CacaoVisualClient.LOGGER.error("Failed to create directory for json file: {}", file.getAbsolutePath(), e);
            return;
        }

        try (Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            CacaoVisualClient.LOGGER.error("Failed to save json to file: {}", file.getName(), e);
        }
    }
}
