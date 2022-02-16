package ru.pinkgoosik.kitsun.schedule;

import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class MCUpdatesSchedule {

    public static void schedule() {
        try {
            ServerUtils.forEach((serverData) -> {
                if(serverData.mcUpdatesPublisher.enabled) {
                    serverData.mcUpdatesPublisher.check();
                }
            });
        }catch (Exception e) {
            Bot.LOGGER.error("Failed to schedule mc updates publishers duo to an exception:\n" + e);
        }
    }
}
