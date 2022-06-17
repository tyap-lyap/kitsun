package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebug;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class QuiltUpdatesScheduler {

    public static void schedule() {
        try {
            ServerUtils.forEach((serverData) -> {
                if(serverData.quiltUpdatesPublisher.enabled) {
                    serverData.quiltUpdatesPublisher.check();
                }
            });
        }
        catch (Exception e) {
            String msg = "Failed to schedule quilt updates publishers duo to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, true);
        }
    }
}
