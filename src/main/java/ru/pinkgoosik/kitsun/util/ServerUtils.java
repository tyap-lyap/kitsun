package ru.pinkgoosik.kitsun.util;

import discord4j.common.util.Snowflake;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;

public class ServerUtils {

    public static void forEach(ServerRunnable runnable) {
        Bot.rest.getGuilds().all(userGuildData -> {
            String serverId = userGuildData.id().asString();
            runnable.run(ServerData.get(serverId));
            return true;
        }).block();
    }

    public static void runFor(String serverId, ServerRunnable runnable) {
        Bot.rest.getGuilds().all(userGuildData -> {
            if(userGuildData.id().asString().equals(serverId)) {
                runnable.run(ServerData.get(serverId));
            }
            return true;
        }).block();
    }

    public static void runFor(Snowflake serverId, ServerRunnable runnable) {
        Bot.rest.getGuilds().all(userGuildData -> {
            if(userGuildData.id().asString().equals(serverId.asString())) {
                runnable.run(ServerData.get(serverId.asString()));
            }
            return true;
        }).block();
    }

    @FunctionalInterface
    public interface ServerRunnable {
        void run(ServerData serverData);
    }
}
