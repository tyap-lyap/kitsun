package ru.pinkgoosik.kitsun.config;

public class Secrets {
    public String discordBotToken;
    public String ftpHostIp;
    public String ftpUserName;
    public String ftpPassword;
    public String saveDir;

    public static final Secrets DEFAULT = new Secrets("", "", "", "", "");

    public Secrets(
            String discordBotToken,
            String ftpHostIp,
            String ftpUserName,
            String ftpPassword,
            String saveDir
    ) {
        this.discordBotToken = discordBotToken;
        this.ftpHostIp = ftpHostIp;
        this.ftpUserName = ftpUserName;
        this.ftpPassword = ftpPassword;
        this.saveDir = saveDir;
    }
}
