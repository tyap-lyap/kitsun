package ru.pinkgoosik.kitsun.instance;

import java.util.ArrayList;

public class ServerDataManager {
    private static final ArrayList<ServerData> DATA = new ArrayList<>();

    public static ServerData getData(String serverID) {
        for (ServerData data : DATA) {
            if(data.serverID.equals(serverID)) return data;
        }
        ServerData serverData = new ServerData(serverID);
        DATA.add(serverData);
        return serverData;
    }
}
