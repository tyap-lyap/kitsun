package ru.pinkgoosik.somikbot.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class UuidGetter {
    public static String URL_STRING = "https://api.mojang.com/users/profiles/minecraft/%nickname%";

    public static String getUuid(String nickname){
        String uuid = null;
        try {
            uuid = tryToParse(nickname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(uuid != null){
            return fromTrimmed(uuid);
        }
        return null;
    }

    public static String tryToParse(String nickname) throws IOException {
        URL url = new URL(URL_STRING.replace("%nickname%", nickname));
        URLConnection request = url.openConnection();
        request.connect();
        JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
        return jsonElement.getAsJsonObject().get("id").getAsString();
    }

    public static String fromTrimmed(String trimmedUUID) throws IllegalArgumentException {
        if(trimmedUUID == null) throw new IllegalArgumentException();
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        /* Backwards adding to avoid index adjustments */
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException();
        }

        return builder.toString();
    }
}
