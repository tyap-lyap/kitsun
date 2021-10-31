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

public class PlayerCloaks {
    private static final String URL_STRING = "https://pinkgoosik.ru/data/cloaks.json";

    public static final ArrayList<String> CLOAKS = new ArrayList<>(List.of("uni", "azure", "crimson", "flamingo", "golden",
            "lapis", "military", "mint", "mystic", "pumpkin", "smoky", "turtle", "violet", "void", "coffee"));
    public static final ArrayList<Entry> ENTRIES = new ArrayList<>();

    public static void grantCloak(String discordId, String nickname, String uuid, String cloak){
        ENTRIES.add(new Entry(discordId, nickname, uuid, cloak));
    }

    public static void revokeCloak(String nickname){
        ENTRIES.removeIf(entry -> entry.name().equals(nickname));
    }

    public static boolean hasCloak(String nickname){
        ArrayList<String> nicknames = new ArrayList<>();
        ENTRIES.forEach(entry -> nicknames.add(entry.name()));
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
                String discord, name, uuid, cloak;
                discord = object.get("discord").getAsString();
                name = object.get("name").getAsString();
                uuid = object.get("uuid").getAsString();
                cloak = object.get("cloak").getAsString();
                grantCloak(discord, name, uuid, cloak);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public record Entry(String discord, String name, String uuid, String cloak) {}
}
