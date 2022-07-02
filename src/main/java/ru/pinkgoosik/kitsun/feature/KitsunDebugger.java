package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import ru.pinkgoosik.kitsun.Bot;

public class KitsunDebugger {
    private static final String channel = "967506328190877726";

    public static void ping(String message) {
        report("<@287598520268095488>\n" + message);
    }

    public static void report(String message) {
        Bot.LOGGER.error(message);
        sendMessage(message);
    }

    public static void info(String text) {
        sendMessage(text);
    }

    private static void sendMessage(String text) {
        try {
            Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(text).block();
        }
        catch (Exception e) {
            Bot.LOGGER.error("Failed to send debug message due to an exception:\n" + e);
        }
    }

}
