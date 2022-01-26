package ru.pinkgoosik.kitsun.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.cosmetica.PlayerCloaks;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
import java.util.Properties;

public class FtpConnection {
    private static Channel channel;

    public static void connect() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(Config.secrets.ftpUserName, Config.secrets.ftpHostIp, 22);
            session.setPassword(Config.secrets.ftpPassword);
            Properties props = new Properties();
            props.put("StrictHostKeyChecking", "no");
            session.setConfig(props);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            Bot.LOGGER.info("Sftp channel opened and connected.");
        } catch (Exception e) {
            Config.general.cloaksEnabled = false;
            Bot.LOGGER.info("Failed to connect due to an exception: " + e);
        }
    }

    public static void updateData(){
        connect();
        createCapesJson();
        try {
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.cd(Config.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/cache/entries.json");
            channelSftp.put(new FileInputStream(file), "entries.json");
            Bot.LOGGER.info("Remote Cloaks Data successfully updated.");
        } catch (SftpException | FileNotFoundException e) {
            Bot.LOGGER.info("Failed to update Remote Cloaks Data due to an exception: " + e);
        }
    }

    private static void createCapesJson(){
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("cache");

            try (FileWriter writer = new FileWriter("cache/entries.json")) {
                writer.write(gson.toJson(PlayerCloaks.ENTRIES));
            }
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create Cloaks Json due to an exception: " + e);
        }
    }
}
