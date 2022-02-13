package ru.pinkgoosik.kitsun.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

public class CachedData<T> {
    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    public Defaulter<T> defaulter;
    public String path;
    public String file;
    public int cachedHashCode = 0;

    public CachedData(String path, String file, Defaulter<T> defaulter) {
        this.defaulter = defaulter;
        this.path = path;
        this.file = file;
    }

    public T read(Class<T> tClass) {
        try {
            String filePath;
            if(path.isBlank()) filePath = this.file;
            else filePath = path + "/" + file;
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            return GSON.fromJson(reader, tClass);
        } catch (Exception e) {
            return defaulter.create();
        }
    }

    public void save(T object) {
        if (cachedHashCode != object.hashCode()) {
            try {
                this.cachedHashCode = object.hashCode();
                FileUtils.createDir(path);
                try (FileWriter writer = new FileWriter(path + "/" + file)) {
                    writer.write(GSON.toJson(object));
                }
            } catch (Exception e) {
                Bot.LOGGER.info("Failed to save cached data due to an exception: " + e);
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    public interface Defaulter<T> {
        T create();
    }
}
