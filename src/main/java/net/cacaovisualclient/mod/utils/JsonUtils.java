package net.cacaovisualclient.mod.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import net.cacaovisualclient.mod.CacaoVisualClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@UtilityClass
public class JsonUtils {

    private static final Gson DEFAULT_GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger LOGGER = LoggerFactory.getLogger(CacaoVisualClient.MOD_ID);

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
            LOGGER.error("Failed to load json from file: {}", file.getName(), e);
        }
        return null;
    }

    public boolean saveToJson(File file, Object object) {
        return saveToJson(DEFAULT_GSON, file, object);
    }

    public boolean saveToJson(Gson gson, File file, Object object) {
        final Path target = file.toPath().toAbsolutePath();
        Path temporary = null;

        try {
            Files.createDirectories(target.getParent());
            temporary = Files.createTempFile(target.getParent(), "." + file.getName() + "-", ".tmp");

            try (Writer writer = Files.newBufferedWriter(temporary, StandardCharsets.UTF_8)) {
                gson.toJson(object, writer);
            }

            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException e) {
            LOGGER.error("Failed to save json to file: {}", file.getName(), e);
            return false;
        } finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                } catch (IOException e) {
                    LOGGER.warn("Failed to remove temporary json file: {}", temporary, e);
                }
            }
        }
    }
}
