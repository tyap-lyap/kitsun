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

    static Session session;
    static Channel channel;
    static ChannelSftp channelSftp;

    public static void connect(){
        int port = 22;
        String host = Config.secrets.ftpHostIp;
        String user = Config.secrets.ftpUserName;
        String pass = Config.secrets.ftpPassword;

        Bot.LOGGER.info("Preparing the ftp connection.");

        try {
            JSch jSch = new JSch();
            session = jSch.getSession(user, host, port);
            session.setPassword(pass);
            Properties props = new Properties();
            props.put("StrictHostKeyChecking", "no");
            session.setConfig(props);
            session.connect();
            Bot.LOGGER.info("Host connected.");

            channel = session.openChannel("sftp");
            channel.connect();
            Bot.LOGGER.info("Sftp channel opened and connected.");

        } catch (Exception e) {
            Bot.LOGGER.info("Failed to connect.");
            e.printStackTrace();
        }
    }

    public static void updateCapesData(){
        connect();
        createCapesJson();
        try{
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(Config.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/capes.json");
            channelSftp.put(new FileInputStream(file), "capes.json");
            Bot.LOGGER.info("Remote Capes Data successfully updated.");
        } catch (SftpException | FileNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
