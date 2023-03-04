package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.feature.ModCard;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.List;

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
		modCards.forEach(ModCard::update);
		modCards.removeIf(card -> card.shouldBeRemoved);
		data.modCards.set(modCards.toArray(new ModCard[0]));
		data.modCards.save();
	}
}
