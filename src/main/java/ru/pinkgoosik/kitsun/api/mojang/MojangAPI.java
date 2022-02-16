package ru.pinkgoosik.kitsun.api.mojang;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.pinkgoosik.kitsun.Bot;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class MojangAPI {
    private static final String URL_STRING = "https://api.mojang.com/users/profiles/minecraft/%nickname%";
    private static final String MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";

    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Optional<VersionManifest> getManifest() {
        return parseManifest();
    }

    private static Optional<VersionManifest> parseManifest() {
        try {
            URL url = new URL(MANIFEST);
            URLConnection request = url.openConnection();
            request.connect();
            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            VersionManifest manifest = GSON.fromJson(reader, VersionManifest.class);
            return Optional.of(manifest);
        } catch (Exception e) {
            Bot.LOGGER.error("Failed to parse minecraft versions manifest due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    public static Optional<String> getUuid(String nickname) {
        var uuid = parseUuid(nickname);
        if (uuid.isPresent()) {
            return fromTrimmed(uuid.get());
        }
        return Optional.empty();
    }

    private static Optional<String> parseUuid(String nickname) {
        try {
            URL url = new URL(URL_STRING.replace("%nickname%", nickname));
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(request.getInputStream()));
            return Optional.of(jsonElement.getAsJsonObject().get("id").getAsString());
        }catch (Exception e) {
            Bot.LOGGER.error("Failed to parse " + nickname + " uuid due to an exception:\n" + e);
            return Optional.empty();
        }
    }

    private static Optional<String> fromTrimmed(String trimmedUUID) {
        try {
            StringBuilder builder = new StringBuilder(trimmedUUID.trim());
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
            return Optional.of(builder.toString());
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
