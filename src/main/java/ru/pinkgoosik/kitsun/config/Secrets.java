package ru.pinkgoosik.kitsun.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;

import java.io.*;

public class Secrets {
    public String discordBotToken;
    public String ftpHostIp;
    public String ftpUserName;
    public String ftpPassword;
    public String saveDir;

    public static final Secrets EMPTY = new Secrets("", "", "", "", "");

    public Secrets(
            String discordBotToken,
            String ftpHostIp,
            String ftpUserName,
            String ftpPassword,
            String saveDir
    ) {
        this.discordBotToken = discordBotToken;
        this.ftpHostIp = ftpHostIp;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.saveDir = saveDir;
    }

    public static Secrets readSecrets() {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            BufferedReader reader = new BufferedReader(new FileReader("secrets.json"));
            return gson.fromJson(reader, Secrets.class);
        } catch (FileNotFoundException e) {
            createEmpty();
            return Secrets.EMPTY;
        }
    }

    private static void createEmpty() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();

            FileWriter writer = new FileWriter("secrets.json");
            writer.write(gson.toJson(EMPTY));
            writer.close();
            Bot.LOGGER.error("Please fill the secrets config.");
            System.exit(0);
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty secrets config due to an exception: " + e);
        }
    }
}
