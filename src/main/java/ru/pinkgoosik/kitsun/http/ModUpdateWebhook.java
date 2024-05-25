package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpExchange;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.feature.ModUpdatesPublisher;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;

public class ModUpdateWebhook extends KitsunHttpHandler {

	public static ArrayList<String> publishedVersions = new ArrayList<>();

	@Override
	public void handle(HttpExchange exchange) {
		var map = parseParams(exchange);

		if (map.containsKey("token") && Bot.secrets.get().http.token.equals(map.get("token"))) {
			if(map.containsKey("server") && map.containsKey("project") && map.containsKey("channel")) {
				if (!ServerUtils.exist(map.get("server"))) {
					success(exchange, "Server not found");
					return;
				}

				if(!ChannelUtils.exist(map.get("server"), map.get("channel"))) {
					success(exchange, "Channel not found");
					return;
				}

				success(exchange, "Success");

				try {
					Thread.sleep(5 * 1000);
					var project = Modrinth.getProject(map.get("project"));
					var versions = Modrinth.getVersions(map.get("project"));

					if(project.isPresent() && versions.isPresent() && !publishedVersions.contains(versions.get().get(0).getId()) && Bot.jda.getGuildChannelById(map.get("channel")) instanceof StandardGuildMessageChannel messageChannel) {
						publishedVersions.add(versions.get().get(0).getId());
						messageChannel.sendMessageEmbeds(ModUpdatesPublisher.createEmbed(project.get(), versions.get().get(0))).queue(message -> {}, throwable -> {
							KitsunDebugger.ping("Failed to send update message of the " + project.get() + " project due to an exception:\n" + throwable);
						});
					}
				} catch (Exception ignored) {}

				return;
			}
		}

		notFound(exchange);
	}
}
