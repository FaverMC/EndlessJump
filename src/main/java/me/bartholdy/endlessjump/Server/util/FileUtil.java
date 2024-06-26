package me.bartholdy.endlessjump.Server.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import me.bartholdy.endlessjump.Server.Config;
import me.bartholdy.endlessjump.Server.io.ConfigDeserializer;
import me.bartholdy.endlessjump.Server.io.ConfigSerializer;
import org.jetbrains.annotations.NotNull;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;

public final class FileUtil {

    private FileUtil() {}

    public static @NotNull ObjectTriple<FileResult, Config, Exception> loadConfig(@NotNull Path path) {
        File ConfigFile = path.toFile();
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule("ConfigSerializer");

        module.addDeserializer(Config.class, new ConfigDeserializer());
        module.addSerializer(Config.class, new ConfigSerializer());
        mapper.registerModule(module);

        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        ObjectReader reader = mapper.reader();

        // if file has been created write the default
        // data into the config and return it
        if (createFileIfNeeded(ConfigFile)) {
            try {
                Config Config = new Config();
                writer.writeValue(ConfigFile, new Config());
                return ObjectTriple.of(FileResult.CREATED, Config, null);
            } catch (IOException exc) {
                throw new RuntimeException(exc);
            }
        }

        // if the file already exists deserialize it
        try {
            Config Config = reader.readValue(ConfigFile, Config.class);
            return ObjectTriple.of(FileResult.EXISTING, Config, null);
        } catch (Exception exc) {
            exc.printStackTrace();
            return ObjectTriple.of(FileResult.MALFORMED, null, exc);
        }
    }

    private static boolean createFileIfNeeded(@NotNull File file) {
        try {
            return file.createNewFile();
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
    }

}