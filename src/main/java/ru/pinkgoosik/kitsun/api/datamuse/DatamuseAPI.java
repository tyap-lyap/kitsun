package ru.pinkgoosik.kitsun.api.datamuse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class DatamuseAPI {
    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
    private static final Random RANDOM = new Random();
    public static final String URL = "https://api.datamuse.com/words?rel_jjb=lounge";
    public static final String URL_2 = "https://api.datamuse.com/words?rel_jjb=room";

    public static String getRandomAdjective() {
        var hits = getAdjectives();
        if(hits.isPresent()) {
            var hit = hits.get().get(RANDOM.nextInt(hits.get().size()));
            return hit.word;
        }
        return "gay";
    }

    public static Optional<ArrayList<Hit>> getAdjectives() {
        try {
            URL url = new URL(URL);
            URLConnection request = url.openConnection();
            request.connect();

            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            Hit[] hits = GSON.fromJson(reader, Hit[].class);
            ArrayList<Hit> hitsArray = new ArrayList<>(Arrays.asList(hits));
            return Optional.of(hitsArray);
        }
        catch (Exception e) {
            String msg = "Failed to parse datamuse adjectives due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebugger.report(msg, e, false);
        }
        return Optional.empty();
    }

    public static class Hit {
        public String word = "";
        public int score = 0;
    }
}
