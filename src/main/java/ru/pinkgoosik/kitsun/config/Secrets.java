package ru.pinkgoosik.kitsun.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;

public class Secrets {
    public String discordBotToken;
    public String ftpHostIp;
    public String ftpUserName;
    public String ftpPassword;
    public String saveDir;
    public static final Secrets EMPTY = new Secrets("", "", "", "", "");

    public Secrets(String token, String ip, String user, String pass, String saveDir) {
        this.discordBotToken = token;
        this.ftpHostIp = ip;
        this.ftpUserName = user;
        this.ftpPassword = pass;
        this.saveDir = saveDir;
    }

    public static Secrets init() {
        return readSecrets();
    }

    private static void createEmpty() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("config");

            FileWriter writer = new FileWriter("config/secrets.json");
            writer.write(gson.toJson(EMPTY));
            writer.close();
            Bot.LOGGER.error("Please fill the secrets config.");
            System.exit(0);
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create empty secrets config due to an exception: " + e);
        }
    }

    private static Secrets readSecrets() {
        try{
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            BufferedReader reader = new BufferedReader(new FileReader("config/secrets.json"));
            return gson.fromJson(reader, Secrets.class);
        } catch (FileNotFoundException e) {
            createEmpty();
            return Secrets.EMPTY;
        }
    }
}
