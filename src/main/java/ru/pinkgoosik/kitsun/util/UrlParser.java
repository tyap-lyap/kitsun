package ru.pinkgoosik.kitsun.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UrlParser {
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static <T> T get(String urlStr, Class<T> type) throws Exception {
        URL url = new URL(urlStr);
        URLConnection request = url.openConnection();
        request.connect();
        InputStreamReader reader = new InputStreamReader(request.getInputStream());
        return GSON.fromJson(reader, type);
    }
}
