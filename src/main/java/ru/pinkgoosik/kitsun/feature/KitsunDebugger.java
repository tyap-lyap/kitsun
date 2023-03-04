package ru.pinkgoosik.kitsun.feature;

import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import ru.pinkgoosik.kitsun.Bot;

import java.util.ArrayList;
import java.util.Objects;

public class KitsunDebugger {
	private static String debugChannel = "967506328190877726";
	public static final ArrayList<String> CACHE = new ArrayList<>();

	public static void onConnect(ReadyEvent event) {
		if(event.getJDA().getSelfUser().getId().equals("935826731925913630")) debugChannel = "1081197875830206475";
	}

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
			Objects.requireNonNull(Bot.jda.getChannelById(MessageChannelUnion.class, debugChannel)).sendMessage(text).queue();
//			Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(text).block();
			CACHE.add(text);
		}
		catch(Exception e) {
			Bot.LOGGER.error("Failed to send debug message due to an exception:\n" + e);
		}
	}

}
