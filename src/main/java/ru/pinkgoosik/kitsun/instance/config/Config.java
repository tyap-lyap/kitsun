package ru.pinkgoosik.kitsun.instance.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;

public class Config {
    public final String serverID;
    public final GeneralConfig general;

    public Config(String serverID) {
        this.serverID = serverID;
        this.general = readGeneral();
    }

    public void saveConfig() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config/" + serverID);
            try (FileWriter writer = new FileWriter("config/" + serverID + "/general.json")) {
                writer.write(gson.toJson(general));
            }
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to save config due to an exception: " + e);
        }
    }

    private GeneralConfig readGeneral() {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            BufferedReader reader = new BufferedReader(new FileReader("config/" + serverID + "/general.json"));
            return gson.fromJson(reader, GeneralConfig.class);
        } catch (FileNotFoundException e) {
            createEmptyGeneral();
            return GeneralConfig.EMPTY;
        }
    }

    private void createEmptyGeneral() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config/" + serverID);
            try (FileWriter writer = new FileWriter("config/" + serverID + "/general.json")) {
                writer.write(gson.toJson(GeneralConfig.EMPTY));
            }
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty general config due to an exception: " + e);
        }
    }
}
