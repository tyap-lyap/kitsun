package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import ru.pinkgoosik.kitsun.Bot;

import java.util.ArrayList;

public class KitsunDebugger {
	private static final String channel = "967506328190877726";
	public static final ArrayList<String> CACHE = new ArrayList<>();

	public static void ping(String message) {
		Bot.LOGGER.error(message);
		sendMessage("<@287598520268095488>\n" + message);
	}

	public static void report(String message) {
		Bot.LOGGER.error(message);
		sendMessage(message);
	}

	public static void info(String text) {
		sendMessage(text);
	}

	private static void sendMessage(String text) {
		for(String reported : CACHE) {
			if(reported.equals(text)) return;
		}
		try {
			Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(text).block();
			CACHE.add(text);
		}
		catch(Exception e) {
			Bot.LOGGER.error("Failed to send debug message due to an exception:\n" + e);
		}
	}

}
