package ru.pinkgoosik.somikbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.config.entity.ConfiguredChangelogPublisher;
import ru.pinkgoosik.somikbot.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class General {
    public boolean cloaksEnabled;
    public String memberRoleId;
    public ArrayList<ConfiguredChangelogPublisher> publishers;
    public static final General EMPTY = new General(true, "", new ArrayList<>(List.of(new ConfiguredChangelogPublisher("example", "123"))));

    public General(boolean cloaksEnabled, String memberRoleId, ArrayList<ConfiguredChangelogPublisher> publishers){
        this.cloaksEnabled = cloaksEnabled;
        this.memberRoleId = memberRoleId;
        this.publishers = publishers;
    }

    public static General init(){
        return readGeneral();
    }

    private static void createEmpty(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config");
            FileWriter writer = new FileWriter("config/general.json");
            writer.write(gson.toJson(EMPTY));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty general config due to an exception: " + e);
        }
    }

    private static General readGeneral(){
        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            BufferedReader reader = new BufferedReader(new FileReader("config/general.json"));
            return gson.fromJson(reader, General.class);
        } catch (FileNotFoundException e) {
            createEmpty();
            return General.EMPTY;
        }
    }

    private static void saveGeneral(){
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("general.json");
            writer.write(gson.toJson(Config.general));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to save general config due to an exception: " + e);
        }
    }
}
