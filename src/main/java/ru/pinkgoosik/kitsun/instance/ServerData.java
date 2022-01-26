package ru.pinkgoosik.kitsun.instance;

import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;

public class ServerData {
    public String serverID;
    public Config config;
    public AccessManager accessManager;

    public ServerData(String serverID) {
        this.serverID = serverID;
        this.config = new Config(serverID);
        this.accessManager = new AccessManager(serverID);
        accessManager.initPermissions();
    }
}
