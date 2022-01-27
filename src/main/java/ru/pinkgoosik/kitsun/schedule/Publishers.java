package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.instance.ServerDataManager;
import ru.pinkgoosik.kitsun.instance.config.entity.ChangelogPublisherConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Publishers {
    public static final Map<String, ArrayList<ChangelogPublisherConfig>> CONFIGS_CACHE = new LinkedHashMap<>();
    public static final Map<String, ArrayList<ChangelogPublisher>> PUBLISHERS_CACHE = new LinkedHashMap<>();

    public static void schedule() {
        Bot.client.getGuilds().all(userGuildData -> {
            String serverID = userGuildData.id().asString();
            ArrayList<ChangelogPublisherConfig> publisherConfigs = ServerDataManager.getData(serverID).config.general.changelogPublishers;

            if(CONFIGS_CACHE.get(serverID) == null || CONFIGS_CACHE.get(serverID).size() != publisherConfigs.size()) {
                CONFIGS_CACHE.remove(serverID);
                CONFIGS_CACHE.put(serverID, new ArrayList<>(publisherConfigs));

                ArrayList<ChangelogPublisher> publishersFromConfig = new ArrayList<>();
                publisherConfigs.forEach(config -> {
                    ChangelogPublisher publisher = new ChangelogPublisher(config.mod, config.channel, serverID);
                    publishersFromConfig.add(publisher);
                });
                PUBLISHERS_CACHE.remove(serverID);
                PUBLISHERS_CACHE.put(serverID, publishersFromConfig);
                publishersFromConfig.forEach(ChangelogPublisher::check);
            }else {
                PUBLISHERS_CACHE.get(serverID).forEach(ChangelogPublisher::check);
            }
            return true;
        }).block();
    }
}
