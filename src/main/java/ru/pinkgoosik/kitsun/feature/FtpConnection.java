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
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static Channel channel;

    public static void updateData() {
        connect();
        createJson();
        try {
            ChannelSftp channelSftp = (ChannelSftp)channel;
            channelSftp.cd(Bot.secrets.saveDir);
            File file = new File(System.getProperty("user.dir") + "/cache/entries.json");
            channelSftp.put(new FileInputStream(file), "entries.json");
            Bot.LOGGER.info("Remote Cosmetica Data successfully updated.");
        }
        catch (Exception e) {
            String msg = "Failed to update Remote Cosmetica Data due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
            e.printStackTrace();
        }
    }

    private static void connect() {
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
        }
        catch (Exception e) {
            String msg = "Failed connection to Sftp due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
            e.printStackTrace();
        }
    }

    private static void createJson() {
        try {
            FileUtils.createDir("cosmetica");
            try (FileWriter writer = new FileWriter("cosmetica/entries.json")) {
                writer.write(GSON.toJson(CosmeticaData.ENTRIES));
            }
        }
        catch (Exception e) {
            String msg = "Failed to create Cosmetica Data Json due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
        }
    }

}
