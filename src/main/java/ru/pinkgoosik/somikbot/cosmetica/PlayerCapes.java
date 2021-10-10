package ru.pinkgoosik.somikbot.cosmetica;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import ru.pinkgoosik.somikbot.Bot;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlayerCapes {

    public static final ArrayList<String> CAPES = new ArrayList<>(List.of("uni", "light_green", "purple", "red", "blue",
            "brown", "pink", "green", "light_blue"));
    public static ArrayList<Entry> entries = new ArrayList<>();
    public static final String URL_STRING = "https://pinkgoosik.ru/data/capes.json";


    public static void grantCape(String nickname, String uuid, String cape){
        entries.add(new Entry(nickname, uuid, cape, "normal", "0xFFFFFF"));
    }

    public static void revokeCape(String nickname){
        entries.removeIf(entry -> entry.name().equals(nickname));
    }

    public static boolean hasCape(String nickname){
        ArrayList<String> nicknames = new ArrayList<>();
        entries.forEach(entry -> nicknames.add(entry.name()));
        return nicknames.contains(nickname);
    }

    public static void fillFromUpstream(){
        Bot.LOGGER.info("Loading Player Capes...");
        try {
            init();
        } catch (IOException e) {
            entries = null;
            Bot.LOGGER.warn("Failed to load Player Capes due to an exception: " + e);
        } finally {
            if (entries != null) Bot.LOGGER.info("Player Capes successfully loaded");
        }
    }

    public static void init() throws IOException {
        URL url = new URL(URL_STRING);
        URLConnection request = url.openConnection();
        request.connect();

        JsonArray jsonArray = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent())).getAsJsonArray();
        jsonArray.forEach(jsonElement -> {
            String name = jsonElement.getAsJsonObject().get("name").getAsString();
            String uuid = jsonElement.getAsJsonObject().get("uuid").getAsString();
            String cape = jsonElement.getAsJsonObject().get("cape").getAsString();
//            String type = jsonElement.getAsJsonObject().get("type").getAsString();
//            String color = jsonElement.getAsJsonObject().get("color").getAsString();

            grantCape(name, uuid, cape);
        });
    }

    public record Entry(String name, String uuid, String cape, String type, String color) {}
}
