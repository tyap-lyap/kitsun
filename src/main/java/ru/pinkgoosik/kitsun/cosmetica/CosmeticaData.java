package ru.pinkgoosik.kitsun.cosmetica;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CosmeticaData {
    private static final String URL_STRING = "https://pinkgoosik.ru/cosmetica/entries.json";
    public static final List<EntryData> ENTRIES = new ArrayList<>();

    public static void register(String discord, String name, String uuid) {
        EntryData entry = new EntryData();
        EntryData.UserData user = new EntryData.UserData();
        user.discord = discord; user.name = name; user.uuid = uuid; entry.user = user;

        EntryData.CloakData cloak = new EntryData.CloakData();
        cloak.name = ""; cloak.color = ""; cloak.glint = false; entry.cloak = cloak;

        ENTRIES.add(entry);
    }

    public static void unregister(String discord) {
        ENTRIES.removeIf(entry -> entry.user.discord.equals(discord));
    }

    public static Optional<EntryData> getEntry(String discord) {
        for (var entry : CosmeticaData.ENTRIES) {
            if (entry.user.discord.equals(discord)) return Optional.of(entry);
        }
        return Optional.empty();
    }

    public static Optional<EntryData> getEntryByName(String name) {
        for (var entry : CosmeticaData.ENTRIES) {
            if (entry.user.name.equals(name)) return Optional.of(entry);
        }
        return Optional.empty();
    }

    public static void setCloak(String username, String cloak) {
        for(var entry : ENTRIES) {
            if (entry.user.name.equals(username)) {
                entry.cloak.name = cloak;
            }
        }
    }

    public static void clearCloak(String username) {
        for(var entry : ENTRIES) {
            if (entry.user.name.equals(username)) {
                EntryData.CloakData cloak = new EntryData.CloakData();
                cloak.name = "";
                cloak.color = "";
                cloak.glint = false;
                entry.cloak = cloak;
            }
        }
    }

    public static boolean hasCloak(String nickname) {
        for(var entry : ENTRIES) {
            if(entry.user.name.equals(nickname)) {
                return !entry.cloak.name.isBlank();
            }
        }
        return false;
    }

    public static void fillFromUpstream() {
        try {
            URL url = new URL(URL_STRING);
            URLConnection request = url.openConnection();
            request.connect();
            InputStream stream = request.getInputStream();
            Gson gson = new GsonBuilder().setLenient().setPrettyPrinting().create();
            EntryData[] entries = gson.fromJson(new InputStreamReader(stream), EntryData[].class);
            ENTRIES.addAll(Arrays.asList(entries));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}