package ru.pinkgoosik.somikbot.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.util.FileUtils;

import java.io.*;
import java.util.Properties;

public class FtpConnection {
    private static Channel channel;

    public static void connect(){
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

    public static void updateCapesData(){
        connect();
        createCapesJson();
        try{
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.cd(Config.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/cache/cloaks.json");
            channelSftp.put(new FileInputStream(file), "cloaks.json");
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

            FileWriter writer = new FileWriter("cache/cloaks.json");
            writer.write(gson.toJson(PlayerCloaks.ENTRIES));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create Cloaks Json due to an exception: " + e);
        }
    }
}
