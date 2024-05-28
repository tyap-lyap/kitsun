package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.debug.KitsunDebugWebhook;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class MCUpdatesScheduler {

	public static void schedule() {
		try {
			MojangAPI.getManifest().ifPresent(manifest -> ServerUtils.forEach((data) -> {
				if(data.mcUpdates.get().enabled) {
					data.mcUpdates.get().check(manifest);
				}
			}));

		}
		catch(Exception e) {
			KitsunDebugWebhook.ping("Failed to schedule mc updates publishers duo to an exception:\n" + e);
		}
	}

}
