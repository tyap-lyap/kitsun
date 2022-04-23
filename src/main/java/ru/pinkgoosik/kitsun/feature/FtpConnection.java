package ru.pinkgoosik.kitsun.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.*;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
import java.util.Optional;
import java.util.Properties;

public class FtpConnection {
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final JSch J_SCH = new JSch();

    public static void updateData() {
        try {
            var channel = openChannel();
            if(channel.isPresent()) {
                createJson();
                channel.get().cd(Bot.secrets.saveDir);
                File file = new File(System.getProperty("user.dir") + "/cosmetica/entries.json");
                channel.get().put(new FileInputStream(file), "entries.json");
                Bot.LOGGER.info("Remote Cosmetica Data successfully updated.");
            }

        }
        catch (Exception e) {
            Bot.LOGGER.info("Failed to update Remote Cosmetica Data due to an exception:\n" + e);
            e.printStackTrace();
        }
    }

    private static Optional<ChannelSftp> openChannel() {
        try {
            Session session = J_SCH.getSession(Bot.secrets.ftpUserName, Bot.secrets.ftpHostIp, 22);
            session.setPassword(Bot.secrets.ftpPassword);
            Properties props = new Properties();
            props.put("StrictHostKeyChecking", "no");
            session.setConfig(props);
            session.connect();
            ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
            channel.connect();
            Bot.LOGGER.info("Sftp channel opened and connected.");
            return Optional.of(channel);
        }
        catch (Exception e) {
            Bot.LOGGER.info("Failed connection to Sftp due to an exception:\n" + e);
            e.printStackTrace();
            return Optional.empty();
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
            Bot.LOGGER.info("Failed to create Cosmetica Data Json due to an exception:\n" + e);
        }
    }

}
