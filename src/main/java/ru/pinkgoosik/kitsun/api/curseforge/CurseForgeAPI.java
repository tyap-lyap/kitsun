package ru.pinkgoosik.kitsun.api.curseforge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.api.curseforge.entity.CurseForgeMod;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class CurseForgeAPI {
    public static final String MOD_URL = "https://www.curseforge.com/minecraft/mc-mods/%slug%";
    public static final String API_URL = "https://cfproxy.fly.dev/v1";
    public static final String API_MOD_URL = API_URL + "/mods/%id%";

    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Optional<CurseForgeMod> getMod(String id) {
        try {
            URL url = new URL(API_MOD_URL.replace("%id%", id));
            URLConnection request = url.openConnection();
            request.connect();
            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            CurseForgeMod mod = GSON.fromJson(reader, CurseForgeMod.class);
            return Optional.of(mod);
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse curseforge mod " + id + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }
}
