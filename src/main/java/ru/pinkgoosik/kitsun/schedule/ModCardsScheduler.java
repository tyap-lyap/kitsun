package ru.pinkgoosik.kitsun.schedule;

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ModCardsScheduler {

	public static void schedule() {
		try {
			ServerUtils.forEach(ModCardsScheduler::proceed);
		}
		catch(Exception e) {
			KitsunDebugger.ping("Failed to schedule mod cards duo to an exception:\n" + e);
		}
	}

	private static void proceed(ServerData data) {
		ArrayList<ModCard> modCards = new ArrayList<>(List.of(data.modCards.get()));
		modCards.removeIf(card -> card.shouldBeRemoved);
		data.modCards.set(modCards.toArray(new ModCard[0]));
		data.modCards.save();

		int index = -1;
		for(var card : modCards) {
			index++;

			if(Bot.jda.getGuildChannelById(card.channel) instanceof MessageChannel channel) {
				channel.retrieveMessageById(card.message).queueAfter(index * 10L, TimeUnit.SECONDS, card::update, throwable -> {
					if(throwable.getMessage().contains("Unknown Message")) {
						card.shouldBeRemoved = true;
					}
					else {
						KitsunDebugger.ping("Failed to get " + card.modrinthSlug + " card's message due to an exception:\n" + throwable);
					}
				});
			}
			else {
				card.shouldBeRemoved = true;
			}
		}
	}
}
