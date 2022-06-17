package ru.pinkgoosik.kitsun.instance;

import ru.pinkgoosik.kitsun.cache.CachedData;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.feature.MCUpdatesPublisher;
import ru.pinkgoosik.kitsun.feature.QuiltUpdatesPublisher;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;

import java.util.ArrayList;
import java.util.List;

abstract class CachedServerData {
    /**
     * Discord server ID that this ServerData belongs to
     */
    public String server;

    public ServerConfig config;
    CachedData<ServerConfig> configCache;

    public AccessManager accessManager;
    CachedData<AccessManager> accessManagerCache;

    public ArrayList<ChangelogPublisher> publishers;
    CachedData<ChangelogPublisher[]> publishersCache;

    public ServerLogger logger;
    CachedData<ServerLogger> loggerCache;

    public MCUpdatesPublisher mcUpdatesPublisher;
    CachedData<MCUpdatesPublisher> mcUpdatesPublisherCache;

    public QuiltUpdatesPublisher quiltUpdatesPublisher;
    CachedData<QuiltUpdatesPublisher> quiltUpdatesPublisherCache;

    public AutoChannelsManager autoChannelsManager;
    CachedData<AutoChannelsManager> autoChannelsManagerCache;

    public CachedServerData(String serverId) {
        this.server = serverId;

        this.configCache = new CachedData<>("data/" + serverId, "config.json", () -> new ServerConfig(serverId));
        this.config = configCache.read(ServerConfig.class);

        this.accessManagerCache = new CachedData<>("data/" + serverId, "permissions.json", () -> new AccessManager(serverId));
        this.accessManager = accessManagerCache.read(AccessManager.class);

        this.publishersCache = new CachedData<>("data/" + serverId, "publishers.json", () -> new ChangelogPublisher[]{});
        publishers = new ArrayList<>(List.of(publishersCache.read(ChangelogPublisher[].class)));

        this.loggerCache = new CachedData<>("data/" + serverId, "logger.json", () -> new ServerLogger(serverId));
        this.logger = loggerCache.read(ServerLogger.class);

        this.mcUpdatesPublisherCache = new CachedData<>("data/" + serverId, "mc_updates.json", () -> new MCUpdatesPublisher(serverId));
        this.mcUpdatesPublisher = mcUpdatesPublisherCache.read(MCUpdatesPublisher.class);

        this.quiltUpdatesPublisherCache = new CachedData<>("data/" + serverId, "quilt_updates.json", () -> new QuiltUpdatesPublisher(serverId));
        this.quiltUpdatesPublisher = quiltUpdatesPublisherCache.read(QuiltUpdatesPublisher.class);

        this.autoChannelsManagerCache = new CachedData<>("data/" + serverId, "auto_channels.json", () -> new AutoChannelsManager(serverId));
        this.autoChannelsManager = autoChannelsManagerCache.read(AutoChannelsManager.class);
    }

    /**
     * Should be called after modifying anything in this ServerData
     */
    public void save() {
        configCache.save(config);
        accessManagerCache.save(accessManager);
        publishersCache.save(publishers.toArray(ChangelogPublisher[]::new));
        loggerCache.save(logger);
        mcUpdatesPublisherCache.save(mcUpdatesPublisher);
        quiltUpdatesPublisherCache.save(quiltUpdatesPublisher);
        autoChannelsManagerCache.save(autoChannelsManager);
    }

}
