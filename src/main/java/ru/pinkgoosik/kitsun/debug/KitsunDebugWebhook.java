package ru.pinkgoosik.kitsun.debug;

import ru.pinkgoosik.kitsun.DiscordApp;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KitsunDebugWebhook {
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

	public static void ping(String message) {
		DiscordApp.LOGGER.error(message);
		sendMessage("<@287598520268095488>\n" + message);
	}

	public static void report(String message) {
		DiscordApp.LOGGER.error(message);
		sendMessage(message);
	}

	public static void info(String text) {
		DiscordApp.LOGGER.info(text);
		sendMessage(text);
	}

	private static void sendMessage(String content) {
		if(DiscordApp.secrets.get().debugWebhook.isBlank()) return;

		try {
			byte[] out = ("{\"content\":\"" + content + "\"}").getBytes(StandardCharsets.UTF_8);
			int length = out.length;

			HttpRequest request = HttpRequest.newBuilder()
				.POST(HttpRequest.BodyPublishers.ofByteArray(out))
				.uri(new URI(DiscordApp.secrets.get().debugWebhook))
				.header("Content-Type", "application/json")
				.build();

			HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
		}
		catch (Exception e) {
			DiscordApp.LOGGER.info("Failed to execute webhook due to an exception: " + e);
		}
	}

}
