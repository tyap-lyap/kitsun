package ru.pinkgoosik.kitsun.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
import java.util.Properties;

public class FtpConnection {
    private static Channel channel;

    public static void connect() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(Bot.secrets.ftpUserName, Bot.secrets.ftpHostIp, 22);
            session.setPassword(Bot.secrets.ftpPassword);
            Properties props = new Properties();
            props.put("StrictHostKeyChecking", "no");
            session.setConfig(props);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            Bot.LOGGER.info("Sftp channel opened and connected.");
        } catch (Exception e) {
            Bot.LOGGER.info("Failed to connect due to an exception: " + e);
        }
    }

    public static void updateData() {
        connect();
        createJson();
        try {
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.cd(Bot.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/cosmetica/entries.json");
            channelSftp.put(new FileInputStream(file), "entries.json");
            Bot.LOGGER.info("Remote Cosmetica Data successfully updated.");
        } catch (Exception e) {
            Bot.LOGGER.info("Failed to update Remote Cosmetica Data due to an exception: " + e);
        }
    }

    private static void createJson() {
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("cosmetica");

            try (FileWriter writer = new FileWriter("cosmetica/entries.json")) {
                writer.write(gson.toJson(CosmeticaData.ENTRIES));
            }
        } catch (Exception e) {
            Bot.LOGGER.info("Failed to create Cosmetica Data Json due to an exception: " + e);
        }
    }
}
