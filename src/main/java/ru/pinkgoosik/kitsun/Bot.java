package ru.pinkgoosik.kitsun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.GatewayIntent;
import ru.pinkgoosik.kitsun.cache.Cached;
import ru.pinkgoosik.kitsun.config.Secrets;
import ru.pinkgoosik.kitsun.cosmetics.CosmeticsData;
import ru.pinkgoosik.kitsun.event.DiscordEventsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Bot {
	public static final Logger LOGGER = LoggerFactory.getLogger("Kitsun");
	public static Cached<Secrets> secrets = Cached.of("secrets", Secrets.class, () -> Secrets.DEFAULT);
	public static JDA jda;

	public static void main(String[] args) throws Exception {
		CosmeticsData.fetch();
		Bot.init();
	}

	public static void init() throws InterruptedException {
		String token = secrets.get().discordToken;
		if(token.isBlank()) {
			LOGGER.error("Discord token is blank, please modify secrets.json");
			System.exit(0);
		}

		JDA jda = JDABuilder.createDefault(token)
				.addEventListeners((EventListener) event -> {
					if(event instanceof ReadyEvent readyEvent) {
						DiscordEventsListener.onConnect(readyEvent);
					}
				})
				.addEventListeners(new DiscordEventsListener())
				.enableIntents(Arrays.asList(GatewayIntent.values()))
				.build();

		jda.awaitReady();
	}

	public static Guild getGuild(String id) {
		return jda.getGuildById(id);
	}

}
