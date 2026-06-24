package net.cacaovisualclient.mod.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import net.cacaovisualclient.mod.CacaoVisualClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@UtilityClass
public class JsonUtils {

    private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();

    public <T> T loadFromJson(File file, Class<T> clazz) {
        return loadFromJson(DEFAULT_GSON, file, clazz);
    }

    public <T> T loadFromJson(Gson gson, File file, Class<T> clazz) {
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            CacaoVisualClient.LOGGER.error("Failed to load json from file: {}", file.getName(), e);
        }
        return null;
    }

    public void saveToJson(File file, Object object) {
        saveToJson(DEFAULT_GSON, file, object);
    }

    public void saveToJson(Gson gson, File file, Object object) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(object, writer);
        } catch (IOException e) {
            CacaoVisualClient.LOGGER.error("Failed to save json to file: {}", file.getName(), e);
        }
    }
}
