package ru.pinkgoosik.kitsun.util;

import discord4j.common.util.Snowflake;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.instance.ServerData;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerUtils {

    public static boolean hasChannel(String serverId, String channelId) {
        AtomicBoolean channelExist = new AtomicBoolean(false);

        Bot.client.getGuildById(Snowflake.of(serverId)).getChannels().all(channelData -> {
            if(channelData.id().asString().equals(channelId)) {
                channelExist.set(true);
            }
            return true;
        }).block();

        return channelExist.get();
    }

    public static void forEach(ServerRunnable runnable) {
        Bot.client.getGuilds().all(userGuildData -> {
            String serverId = userGuildData.id().asString();
            runnable.run(ServerData.get(serverId));
            return true;
        }).block();
    }

    public static void runFor(String serverId, ServerRunnable runnable) {
        Bot.client.getGuilds().all(userGuildData -> {
            if(userGuildData.id().asString().equals(serverId)) {
                runnable.run(ServerData.get(serverId));
            }
            return true;
        }).block();
    }

    public static void runFor(Snowflake serverId, ServerRunnable runnable) {
        Bot.client.getGuilds().all(userGuildData -> {
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
