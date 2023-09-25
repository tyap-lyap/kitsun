package ru.pinkgoosik.kitsun;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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

	public static void main(String[] args) {
		CosmeticsData.fetch();
		Bot.init();
	}

	public static void init() {
		try {
			String token = secrets.get().discordToken;
			if(token.isBlank()) {
				LOGGER.error("Discord token is blank, please modify secrets.json");
				System.exit(0);
			}

			var jda = JDABuilder.createDefault(token)
				.addEventListeners((EventListener) event -> {
					if(event instanceof ReadyEvent readyEvent) {
						DiscordEventsListener.onConnect(readyEvent);
					}
				})
				.addEventListeners(new DiscordEventsListener())
				.enableIntents(Arrays.asList(GatewayIntent.values()));

			if(!secrets.get().activity.isBlank()) {
				jda.setActivity(Activity.playing(secrets.get().activity));
			}

			jda.build().awaitReady();
		}
		catch (Exception e) {
			System.out.println("Bot can't be started due to an exception: " + e);
		}

	}

	public static Guild getGuild(String id) {
		return jda.getGuildById(id);
	}

}
