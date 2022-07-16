package ru.pinkgoosik.kitsun.api.curseforge;

import ru.pinkgoosik.kitsun.api.curseforge.entity.CurseForgeMod;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.UrlParser;

import java.io.FileNotFoundException;
import java.util.Optional;

public class CurseForgeAPI {
    public static final String API_URL = "https://cfproxy.fly.dev/v1";
    public static final String API_MOD_URL = API_URL + "/mods/%id%";

    public static Optional<CurseForgeMod> getMod(String id) {
        try {
            return Optional.of(UrlParser.get(API_MOD_URL.replace("%id%", id), CurseForgeMod.class));
        }
        catch (FileNotFoundException e) {
            return Optional.empty();
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse curseforge mod " + id + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }
}
