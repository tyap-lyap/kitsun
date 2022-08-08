package ru.pinkgoosik.kitsun.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.function.Consumer;

public class Cached<T> {
	public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	public DefaultBuilder<T> defaultBuilder;
	public String path;
	public String file;

	T data;

	public Cached(String path, String file, Class<T> type, DefaultBuilder<T> defaultBuilder) {
		this.defaultBuilder = defaultBuilder;
		this.path = path;
		this.file = file;
		this.data = this.read(type);
	}

	public static <T> Cached<T> of(String path, String file, Class<T> type, DefaultBuilder<T> defaultBuilder) {
		String fileName = file.contains(".json") ? file : file + ".json";
		return new Cached<>(path, fileName, type, defaultBuilder);
	}

	public static <T> Cached<T> of(String file, Class<T> type, DefaultBuilder<T> defaultBuilder) {
		return Cached.of("", file, type, defaultBuilder);
	}

	public T read(Class<T> type) {
		String filePath = path.isBlank() ? this.file : this.path + "/" + this.file;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			return GSON.fromJson(reader, type);
		}
		catch(FileNotFoundException e) {
			Bot.LOGGER.info("File " + filePath + " is not found! Setting to default.");
			return defaultBuilder.create();
		}
		catch(Exception e) {
			Bot.LOGGER.error("Failed to read cached data due to an exception:\n" + e + "\n Setting to default.");
			return defaultBuilder.create();
		}
	}

	public void save() {
		try {
			String filePath = path.isBlank() ? this.file : this.path + "/" + this.file;
			FileUtils.createDir(this.path);
			try(FileWriter writer = new FileWriter(filePath)) {
				writer.write(GSON.toJson(this.data));
			}
		}
		catch(Exception e) {
			KitsunDebugger.ping("Failed to save cached data due to an exception:\n" + e);
			e.printStackTrace();
		}
	}

	public T get() {
		return this.data;
	}

	public void get(Consumer<T> consumer) {
		consumer.accept(this.data);
	}

	public void modify(Consumer<T> consumer) {
		consumer.accept(this.data);
		this.save();
	}

	public void set(T replacement) {
		this.data = replacement;
	}

	@FunctionalInterface
	public interface DefaultBuilder<T> {
		T create();
	}

}
