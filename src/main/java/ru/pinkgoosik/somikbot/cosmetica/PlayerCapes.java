package ru.pinkgoosik.somikbot.cosmetica;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlayerCapes {
    public static final ArrayList<String> CAPES = new ArrayList<>(List.of("uni", "light_green", "purple", "red", "blue",
            "brown", "pink", "green", "light_blue", "trans"));
    public static final String URL_STRING = "https://pinkgoosik.ru/data/capes.json";
    public static ArrayList<Entry> entries = new ArrayList<>();

    public static void grantCape(String discordId, String nickname, String uuid, String cape){
        entries.add(new Entry(discordId, nickname, uuid, cape, "normal", "0xFFFFFF"));
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
        try{
            URL url = new URL(URL_STRING);
            URLConnection request = url.openConnection();
            request.connect();
            InputStream stream = request.getInputStream();
            JsonArray array = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonArray();
            array.forEach(entry -> {
                JsonObject object = entry.getAsJsonObject();
                String id, name, uuid, cape;
                id = object.get("id").getAsString();
                name = object.get("name").getAsString();
                uuid = object.get("uuid").getAsString();
                cape = object.get("cape").getAsString();
                grantCape(id, name, uuid, cape);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record Entry(String id, String name, String uuid, String cape, String type, String color) {}
}
