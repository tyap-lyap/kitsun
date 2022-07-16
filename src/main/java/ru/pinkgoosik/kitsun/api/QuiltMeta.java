package ru.pinkgoosik.kitsun.api;

import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.UrlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuiltMeta {
    public static final String QUILT_VERSIONS_URL = "https://meta.quiltmc.org/v3/versions/loader";

    public static Optional<ArrayList<QuiltVersion>> getQuiltVersions() {
        try {
            QuiltVersion[] versions = UrlParser.get(QUILT_VERSIONS_URL, QuiltVersion[].class);
            return Optional.of(new ArrayList<>(List.of(versions)));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse quilt versions due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    public static class QuiltVersion {
        public String separator = "";
        public int build = 0;
        public String maven = "";
        public String version = "";
    }

}
