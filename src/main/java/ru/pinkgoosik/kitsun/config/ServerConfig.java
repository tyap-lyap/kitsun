package ru.pinkgoosik.kitsun.config;

public class ServerConfig {
    public String serverId;
    public GeneralConfig general = new GeneralConfig();

    public ServerConfig(String serverId) {
        this.serverId = serverId;
    }
}
