package ru.pinkgoosik.kitsun.cosmetics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
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
            channelSftp.cd(Bot.secrets.get().dir);
            File file = new File(System.getProperty("user.dir") + "/cosmetics/data.json");
            channelSftp.put(new FileInputStream(file), "data.json");
            Bot.LOGGER.info("Remote cosmetics data successfully updated.");
        }
        catch (Exception e) {
            KitsunDebugger.ping("Failed to update remote cosmetics data due to an exception:\n" + e);
            e.printStackTrace();
        }
    }

    private static void connect() {
        try {
            JSch jSch = new JSch();
            Session session = jSch.getSession(Bot.secrets.get().user, Bot.secrets.get().host, 22);
            session.setPassword(Bot.secrets.get().password);
            Properties props = new Properties();
            props.put("StrictHostKeyChecking", "no");
            session.setConfig(props);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            Bot.LOGGER.info("Sftp channel opened and connected.");
        }
        catch (Exception e) {
            KitsunDebugger.ping("Failed connection to sftp due to an exception:\n" + e);
            e.printStackTrace();
        }
    }

    private static void createJson() {
        try {
            FileUtils.createDir("cosmetics");
            try (FileWriter writer = new FileWriter("cosmetics/data.json")) {
                writer.write(GSON.toJson(CosmeticsData.ENTRIES));
            }
        }
        catch (Exception e) {
            KitsunDebugger.ping("Failed to create cosmetics data json due to an exception:\n" + e);
        }
    }

}
