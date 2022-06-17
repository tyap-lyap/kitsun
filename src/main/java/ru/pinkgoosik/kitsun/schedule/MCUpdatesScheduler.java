package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.feature.KitsunDebug;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class MCUpdatesScheduler {

    public static void schedule() {
        try {
            MojangAPI.getManifest().ifPresent(manifest -> ServerUtils.forEach((serverData) -> {
                if(serverData.mcUpdatesPublisher.enabled) {
                    serverData.mcUpdatesPublisher.check(manifest);
                }
            }));

        }
        catch (Exception e) {
            String msg = "Failed to schedule mc updates publishers duo to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
        }
    }

}
