package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.feature.KitsunDebug;
import ru.pinkgoosik.kitsun.instance.ServerData;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PublishersScheduler {
    public static final Map<String, ArrayList<ChangelogPublisher>> CACHE = new LinkedHashMap<>();

    public static void schedule() {
        try {
            ServerUtils.forEach(PublishersScheduler::proceed);
        }
        catch (Exception e) {
            String msg = "Failed to schedule publishers duo to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
        }
    }

    private static void proceed(ServerData serverData) {
        ArrayList<ChangelogPublisher> publishers = serverData.publishers;
        String serverId = serverData.server;

        if(CACHE.get(serverId) == null || CACHE.get(serverId).size() != publishers.size()) {
            CACHE.remove(serverId);
            CACHE.put(serverId, new ArrayList<>(publishers));
            publishers.forEach(ChangelogPublisher::check);
        }
        else {
            CACHE.get(serverId).forEach(ChangelogPublisher::check);
        }
    }

}
