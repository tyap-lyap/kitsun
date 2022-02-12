package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.instance.ServerData;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class PublishersSchedule {
    public static final Map<String, ArrayList<ChangelogPublisher>> CACHE = new LinkedHashMap<>();

    public static void schedule() {
        try {
            Bot.client.getGuilds().all(userGuildData -> {
                String serverId = userGuildData.id().asString();
                proceed(serverId);
                return true;
            }).block();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void proceed(String serverId) {
        ArrayList<ChangelogPublisher> publishers = ServerData.getData(serverId).publishers;

        if(CACHE.get(serverId) == null || CACHE.get(serverId).size() != publishers.size()) {
            CACHE.remove(serverId);
            CACHE.put(serverId, new ArrayList<>(publishers));
            CACHE.get(serverId).forEach(ChangelogPublisher::check);
        }else {
            CACHE.get(serverId).forEach(ChangelogPublisher::check);
        }
    }
}
