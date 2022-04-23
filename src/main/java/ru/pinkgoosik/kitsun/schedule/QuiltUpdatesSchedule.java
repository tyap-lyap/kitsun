package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class QuiltUpdatesSchedule {

    public static void schedule() {
        try {
            ServerUtils.forEach((serverData) -> {
                if(serverData.quiltUpdatesPublisher.enabled) {
                    serverData.quiltUpdatesPublisher.check();
                }
            });
        }
        catch (Exception e) {
            Bot.LOGGER.error("Failed to schedule quilt updates publishers duo to an exception:\n" + e);
        }
    }
}
