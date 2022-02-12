package ru.pinkgoosik.kitsun.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class MojangAPI {
    public static final String URL_STRING = "https://api.mojang.com/users/profiles/minecraft/%nickname%";

    public static Optional<String> getUuid(String nickname) {
        var uuid = parse(nickname);
        if (uuid.isPresent()) {
            return fromTrimmed(uuid.get());
        }
        return Optional.empty();
    }

    public static Optional<String> parse(String nickname) {
        try {
            URL url = new URL(URL_STRING.replace("%nickname%", nickname));
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            return Optional.of(jsonElement.getAsJsonObject().get("id").getAsString());
        }catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<String> fromTrimmed(String trimmedUUID) {
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
