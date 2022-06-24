package ru.pinkgoosik.kitsun.config;

public class ServerConfig {
    public String serverId;
    public GeneralConfig general = GeneralConfig.DEFAULT;

    public ServerConfig(String serverId) {
        this.serverId = serverId;
    }
}
