package ru.pinkgoosik.kitsun.instance;

import ru.pinkgoosik.kitsun.cache.CachedData;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;

import java.util.ArrayList;
import java.util.List;

public class ServerData {
    private static final ArrayList<ServerData> DATA_CACHE = new ArrayList<>();
    public String serverId;

    public ServerConfig config;
    public CachedData<ServerConfig> configCache;

    public AccessManager accessManager;
    public CachedData<AccessManager> accessManagerCache;

    public ArrayList<ChangelogPublisher> publishers;
    public CachedData<ChangelogPublisher[]> publishersCache;

    public ServerData(String serverId) {
        this.serverId = serverId;

        this.configCache = new CachedData<>("data/" + serverId, "config.json", () -> new ServerConfig(serverId));
        this.config = configCache.read(ServerConfig.class);

        this.accessManagerCache = new CachedData<>("data/" + serverId, "permissions.json", () -> new AccessManager(serverId));
        this.accessManager = accessManagerCache.read(AccessManager.class);

        this.publishersCache = new CachedData<>("data/" + serverId, "publishers.json", () -> new ChangelogPublisher[]{});
        publishers = new ArrayList<>(List.of(publishersCache.read(ChangelogPublisher[].class)));
    }

    public void saveData() {
        configCache.save(config);
        accessManagerCache.save(accessManager);
        publishersCache.save(publishers.toArray(ChangelogPublisher[]::new));
    }

    public static ServerData getData(String serverId) {
        for (ServerData data : DATA_CACHE) {
            if(data.serverId.equals(serverId)) return data;
        }
        ServerData serverData = new ServerData(serverId);
        DATA_CACHE.add(serverData);
        return serverData;
    }
}
