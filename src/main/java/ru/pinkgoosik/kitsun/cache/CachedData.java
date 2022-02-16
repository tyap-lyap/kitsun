package ru.pinkgoosik.kitsun.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class CachedData<T> {
    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    public DefaultBuilder<T> defaultBuilder;
    public String path;
    public String file;

    public CachedData(String path, String file, DefaultBuilder<T> defaultBuilder) {
        this.defaultBuilder = defaultBuilder;
        this.path = path;
        this.file = file;
    }

    public T read(Class<T> tClass) {
        String filePath;
        if(path.isBlank()) filePath = this.file;
        else filePath = path + "/" + file;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            return GSON.fromJson(reader, tClass);
        } catch (FileNotFoundException e) {
            Bot.LOGGER.info("File " + filePath + " is not found! Setting to default.");
            return defaultBuilder.create();
        } catch (Exception e) {
            Bot.LOGGER.info("Failed to read cached data due to an exception:\n" + e + "\n Setting to default.");
            return defaultBuilder.create();
        }
    }

    public void save(T object) {
        try {
            FileUtils.createDir(path);
            try (FileWriter writer = new FileWriter(path + "/" + file)) {
                writer.write(GSON.toJson(object));
            }
        } catch (Exception e) {
            Bot.LOGGER.error("Failed to save cached data due to an exception:\n" + e);
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    public interface DefaultBuilder<T> {
        T create();
    }
}
