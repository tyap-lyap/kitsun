package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import ru.pinkgoosik.kitsun.Bot;

public class KitsunDebugger {
    private static final String channel = "967506328190877726";

    public static void report(String message, Exception e, boolean shouldPing) {
        String pinkgoosik = "<@287598520268095488>";

        String text = message;

        if(shouldPing) {
            text = pinkgoosik + "\n" + text;
        }

        trySendMessage(text);
    }

    public static void info(String text) {
        trySendMessage(text);
    }

    private static void trySendMessage(String text) {
        try {
            Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(text).block();
        }
        catch (Exception e) {
            Bot.LOGGER.error("Failed to send debug message due to an exception:\n" + e);
        }
    }

}