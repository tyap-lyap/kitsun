package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.feature.ModUpdatesPublisher;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.ServerUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PublishersScheduler {
	public static final Map<String, ArrayList<ModUpdatesPublisher>> CACHE = new LinkedHashMap<>();

	public static void schedule() {
		try {
			ServerUtils.forEach(PublishersScheduler::proceed);
		}
		catch(Exception e) {
			KitsunDebugger.ping("Failed to schedule mod changelog publishers duo to an exception:\n" + e);
		}
	}

	private static void proceed(ServerData data) {
		ArrayList<ModUpdatesPublisher> publishers = new ArrayList<>(List.of(data.publishers.get()));
		String serverId = data.server;

		if(CACHE.get(serverId) == null || CACHE.get(serverId).size() != publishers.size()) {
			CACHE.remove(serverId);
			CACHE.put(serverId, new ArrayList<>(publishers));
			publishers.forEach(ModUpdatesPublisher::check);
		}
		else {
			CACHE.get(serverId).forEach(ModUpdatesPublisher::check);
		}
	}

}
