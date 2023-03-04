package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
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
			KitsunDebugger.ping("Failed to schedule quilt updates publishers duo to an exception:\n" + e);
		}
	}
}
