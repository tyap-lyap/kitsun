package ru.pinkgoosik.kitsun.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.config.entity.ConfiguredChangelogPublisher;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class General {
    public boolean cloaksEnabled;
    public final String memberRoleId;
    public String prefix;
    public final List<ConfiguredChangelogPublisher> publishers;
    public static final General EMPTY = new General(true, "", "!", new ArrayList<>(List.of(new ConfiguredChangelogPublisher("example", "123"))));

    public General(boolean cloaksEnabled, String memberRoleId, String prefix, List<ConfiguredChangelogPublisher> publishers) {
        this.cloaksEnabled = cloaksEnabled;
        this.memberRoleId = memberRoleId;
        this.prefix = prefix;
        this.publishers = publishers;
    }

    public static General init() {
        return readGeneral();
    }

    private static void createEmpty() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config");
            try (FileWriter writer = new FileWriter("config/general.json")) {
                writer.write(gson.toJson(EMPTY));
            }
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty general config due to an exception: " + e);
        }
    }

    private static General readGeneral() {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            BufferedReader reader = new BufferedReader(new FileReader("config/general.json"));
            return gson.fromJson(reader, General.class);
        } catch (FileNotFoundException e) {
            createEmpty();
            return General.EMPTY;
        }
    }
}
