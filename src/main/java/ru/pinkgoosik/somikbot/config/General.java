package ru.pinkgoosik.somikbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.somikbot.Bot;

import java.io.*;

public class General {
    public boolean cloaksEnabled;
    public static final General EMPTY = new General(true);

    public General(boolean cloaksEnabled){
        this.cloaksEnabled = cloaksEnabled;
    }

    public static General init(){
        General general = EMPTY;
        try {
            general = readGeneral();
        } catch (FileNotFoundException e) {
            createEmpty();
        }
        return general;
    }

    private static void createEmpty(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("general.json");
            writer.write(gson.toJson(EMPTY));
            writer.close();
            Bot.LOGGER.info("Please fill the general config.");
            System.exit(0);
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty general config due to an exception: " + e);
        }
    }

    private static General readGeneral() throws FileNotFoundException {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        BufferedReader reader = new BufferedReader(new FileReader("general.json"));
        return gson.fromJson(reader, General.class);
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
