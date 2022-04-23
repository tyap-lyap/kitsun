package ru.pinkgoosik.kitsun.api;

import com.google.gson.*;
import ru.pinkgoosik.kitsun.Bot;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuiltMeta {
    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    public static final String QUILT_VERSIONS_URL = "https://meta.quiltmc.org/v3/versions/loader";


    public static Optional<ArrayList<QuiltVersion>> getQuiltVersions() {
        return parseQuiltVersions();
    }

    private static Optional<ArrayList<QuiltVersion>> parseQuiltVersions() {
        try {
            URL url = new URL(QUILT_VERSIONS_URL);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(request.getInputStream()));
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            QuiltVersion[] versions = GSON.fromJson(jsonArray, QuiltVersion[].class);
            return Optional.of(new ArrayList<>(List.of(versions)));
        }
        catch (Exception e) {
            Bot.LOGGER.error("Failed to parse quilt versions due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    public static class QuiltVersion {
        public String separator = "";
        public int build = 0;
        public String maven = "";
        public String version = "";
    }

}
