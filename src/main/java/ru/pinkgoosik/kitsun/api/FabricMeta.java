package ru.pinkgoosik.kitsun.api;

import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.UrlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FabricMeta {
    public static final String FABRIC_VERSIONS_URL = "https://meta.fabricmc.net/v1/versions/loader";

    public static Optional<ArrayList<Loader>> getFabricVersions() {
        try {
            Loader[] versions = UrlParser.get(FABRIC_VERSIONS_URL, Loader[].class);
            return Optional.of(new ArrayList<>(List.of(versions)));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse fabric versions due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    public static Optional<ArrayList<Entry>> getFabricVersions(String gameVersion) {
        try {
            Entry[] entries = UrlParser.get(FABRIC_VERSIONS_URL + "/" + gameVersion, Entry[].class);
            return Optional.of(new ArrayList<>(List.of(entries)));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse fabric versions of game version " + gameVersion + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    public static class Entry {
        public Loader loader = new Loader();
        public Mappings mappings = new Mappings();
    }

    @SuppressWarnings("unused")
    public static class Loader {
        public String separator = "";
        public int build = 0;
        public String maven = "";
        public String version = "";
        public boolean stable = true;
    }

    @SuppressWarnings("unused")
    public static class Mappings {
        public String gameVersion = "";
        public String separator = "";
        public int build = 0;
        public String maven = "";
        public String version = "";
        public boolean stable = true;
    }
}
