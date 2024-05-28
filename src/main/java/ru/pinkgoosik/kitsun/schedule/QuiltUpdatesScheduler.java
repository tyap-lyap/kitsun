package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.debug.KitsunDebugWebhook;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class QuiltUpdatesScheduler {

	public static void schedule() {
		try {
			ServerUtils.forEach((data) -> {
				if(data.quiltUpdates.get().enabled) {
					data.quiltUpdates.get().check();
				}
			});
		}
		catch(Exception e) {
			KitsunDebugWebhook.ping("Failed to schedule quilt updates publishers duo to an exception:\n" + e);
		}
	}
}
