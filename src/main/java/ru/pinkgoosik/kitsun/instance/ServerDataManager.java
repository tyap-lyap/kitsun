package ru.pinkgoosik.kitsun.instance;

import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;

import java.util.ArrayList;

public class ServerDataManager {
    private static final ArrayList<ServerData> DATA = new ArrayList<>();

    public static Config getConfig(String serverID) {
        for (ServerData server : DATA) {
            if(server.serverID.equals(serverID)) return server.config;
        }
        ServerData server = new ServerData(serverID);
        DATA.add(server);
        return server.config;
    }

    public static AccessManager getAccessManager(String serverID) {
        for (ServerData server : DATA) {
            if(server.serverID.equals(serverID)) return server.accessManager;
        }
        ServerData server = new ServerData(serverID);
        DATA.add(server);
        return server.accessManager;
    }
}
