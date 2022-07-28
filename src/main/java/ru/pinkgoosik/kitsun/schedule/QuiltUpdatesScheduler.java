package ru.pinkgoosik.kitsun.schedule;

import discord4j.rest.http.client.ClientException;
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
		catch(ClientException e) {
			if(e.getMessage().contains("Missing Permissions")) {

			}
			else {
				KitsunDebugger.ping("Failed to schedule quilt updates publishers duo to an exception:\n" + e);
			}
		}
		catch(Exception e) {
			KitsunDebugger.ping("Failed to schedule quilt updates publishers duo to an exception:\n" + e);
		}
	}
}
