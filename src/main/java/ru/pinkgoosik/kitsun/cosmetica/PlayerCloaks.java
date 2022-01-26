package ru.pinkgoosik.kitsun.cosmetica;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class PlayerCloaks {
    private static final String URL_STRING = "https://pinkgoosik.ru/data/entries.json";

    public static final List<String> COLORED_CLOAKS = new ArrayList<>(List.of("uni", "azure", "crimson", "flamingo", "golden",
            "lapis", "military", "mint", "mystic", "pumpkin", "smoky", "turtle", "violet", "void", "coffee"));
    public static final List<String> PRIDE_CLOAKS = new ArrayList<>(List.of("pride", "trans", "lesbian", "gay", "pan", "bi", "non-binary",
            "genderfluid", "aromantic", "demiromantic", "asexual", "demisexual"));
    public static final List<String> PATTERNED_CLOAKS = new ArrayList<>(List.of("space"));
    public static final List<String> CLOAK_EFFECTS = new ArrayList<>(List.of("jeb_", "glint", "enchanted-jeb_", "cosmic", "swirly"));

    public static final List<String> COSMETICS = new ArrayList<>(List.of("flower_crown", "flame_hand", "backpack", "glasses", "crown", "wooden_leg", "robot_leg", "cat_ears", "headset", "paw", "deadmau5_ears"));

    public static final List<String> ATTRIBUTES = new ArrayList<>(List.of("dinnerbone", "glint", "jeb_", "cosmic", "baby", "huge_head", "bobble_head"));

    public static final List<Code> CODES = new ArrayList<>();

    public static final List<Entry> ENTRIES = new ArrayList<>();

    public static void grantCloak(Entry entry) {
        ENTRIES.add(entry);
    }

    public static void register(String discord, String name, String uuid) {
        Entry entry = new Entry();
        Entry.User user = new Entry.User();
        user.discord = discord;
        user.name = name;
        user.uuid = uuid;
        user.patreon = "";
        user.kofi = "";
        entry.user = user;

        Entry.Cloak cloak = new Entry.Cloak();
        cloak.name = "";
        cloak.color = "";
        cloak.glint = false;
        entry.cloak = cloak;

        ENTRIES.add(entry);
    }

    public static void grantCloak(String username, String cloak) {
        ENTRIES.forEach(entry1 -> {
            if (entry1.user.name.equals(username)) {
                entry1.cloak.name = cloak;
            }
        });
    }

    public static void editCloak(String username, String cloak) {
        ENTRIES.forEach(entry1 -> {
            if (entry1.user.name.equals(username)) {
                entry1.cloak.name = cloak;
            }
        });
    }

    public static void revokeCloak(String username) {
        ENTRIES.forEach(entry -> {
            if (entry.user.name.equals(username)) {
                Entry.Cloak cloak = new Entry.Cloak();
                cloak.name = "";
                cloak.color = "";
                cloak.glint = false;
                entry.cloak = cloak;
            }
        });
    }

    public static boolean hasCloak(String nickname) {
        ArrayList<String> usernames = new ArrayList<>();
        ENTRIES.forEach(entry -> usernames.add(entry.user.name));
        return usernames.contains(nickname);
    }

    public static void fillFromUpstream() {
        try {
            URL url = new URL(URL_STRING);
            URLConnection request = url.openConnection();
            request.connect();
            InputStream stream = request.getInputStream();
            Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
            Entry[] entry = GSON.fromJson(new InputStreamReader(stream), Entry[].class);
            for (Entry entry1 : entry) {
                grantCloak(entry1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Code {
        public String code;
        public String type;
        public Entry.Cloak cloak;
        public List<String> attributes = new ArrayList<>();
        public List<Entry.Cosmetic> cosmetics = new ArrayList<>();
    }

    public static class Entry {
        public Entry.User user;
        public Cloak cloak;
        public List<String> attributes = new ArrayList<>();
        public List<Cosmetic> cosmetics = new ArrayList<>();

        public static class User {
            public String name, uuid, discord, patreon, kofi;
        }
        public static class Cloak {
            public String name, color;
            public boolean glint;
        }
        public static class Cosmetic {
            public String name, placement, color;
        }
    }
}
