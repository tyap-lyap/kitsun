package ru.pinkgoosik.somikbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;

import java.io.*;
import java.util.Properties;

public class FtpConnection {

    Session session;
    Channel channel;
    ChannelSftp channelSftp;

    public void connect(){
        String host = Bot.config.secrets.ftpHostIp;
        int port = 22;
        String user = Bot.config.secrets.ftpUserName;
        String pass = Bot.config.secrets.ftpPassword;

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

    public void updateCapesData(){
        connect();
        createCapesJson();
        try{
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(Bot.config.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/capes.json");
            channelSftp.put(new FileInputStream(file), "capes.json");
            Bot.LOGGER.info("Remote Capes Data successfully updated.");
        } catch (SftpException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createCapesJson(){
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
