package ru.pinkgoosik.kitsun.schedule;

import discord4j.rest.http.client.ClientException;
import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
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
        catch(ClientException e) {
            if(e.getMessage().contains("Missing Permissions")) {

            }
            else {
                KitsunDebugger.ping("Failed to schedule mc updates publishers duo to an exception:\n" + e);
            }
        }
        catch (Exception e) {
            KitsunDebugger.ping("Failed to schedule mc updates publishers duo to an exception:\n" + e);
        }
    }

}
