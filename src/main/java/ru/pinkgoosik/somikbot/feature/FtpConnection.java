package ru.pinkgoosik.somikbot.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;

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
            Config.general.capesEnabled = false;
            Bot.LOGGER.info("Failed to connect due to an exception: " + e);
        }
    }

    public static void updateCapesData(){
        connect();
        createCapesJson();
        try{
            ChannelSftp channelSftp = (ChannelSftp) channel;
            channelSftp.cd(Config.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/capes.json");
            channelSftp.put(new FileInputStream(file), "capes.json");
            Bot.LOGGER.info("Remote Capes Data successfully updated.");
        } catch (SftpException | FileNotFoundException e) {
            Bot.LOGGER.info("Failed to update Remote Capes Data due to an exception: " + e);
        }
    }

    private static void createCapesJson(){
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("capes.json");
            writer.write(gson.toJson(PlayerCapes.entries));
            writer.close();
        } catch (IOException e) {
            Bot.LOGGER.info("Failed to create Capes Json due to an exception: " + e);
        }
    }
}
