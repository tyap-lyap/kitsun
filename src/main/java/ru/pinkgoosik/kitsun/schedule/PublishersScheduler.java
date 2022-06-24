package ru.pinkgoosik.kitsun.schedule;

import discord4j.rest.http.client.ClientException;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PublishersScheduler {
    public static final Map<String, ArrayList<ChangelogPublisher>> CACHE = new LinkedHashMap<>();

    public static void schedule() {
        try {
            ServerUtils.forEach(PublishersScheduler::proceed);
        }
        catch(ClientException e) {
            if(e.getMessage().contains("Missing Permissions")) {

            }
            else {
                String msg = "Failed to schedule mod changelog publishers duo to an exception:\n" + e;
                Bot.LOGGER.error(msg);
                KitsunDebugger.report(msg, e, true);
            }
        }
        catch (Exception e) {
            String msg = "Failed to schedule mod changelog publishers duo to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, true);
        }
    }

    private static void proceed(ServerData serverData) {
        ArrayList<ChangelogPublisher> publishers = new ArrayList<>(List.of(serverData.publishers.get()));
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
