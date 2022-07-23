package ru.pinkgoosik.kitsun.api;

import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.UrlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuiltMeta {
    public static final String QUILT_VERSIONS_URL = "https://meta.quiltmc.org/v3/versions/loader";
    public static final String QM_VERSIONS_URL = "https://meta.quiltmc.org/v3/versions/quilt-mappings";

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

    public static Optional<ArrayList<Entry>> getQuiltVersions(String gameVersion) {
        try {
            Entry[] entries = UrlParser.get(QUILT_VERSIONS_URL + "/" + gameVersion, Entry[].class);
            return Optional.of(new ArrayList<>(List.of(entries)));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse quilt versions for game version " + gameVersion + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    public static Optional<ArrayList<QuiltMappingsVersion>> getQuiltMappingsVersions(String gameVersion) {
        try {
            QuiltMappingsVersion[] versions = UrlParser.get(QM_VERSIONS_URL + "/" + gameVersion, QuiltMappingsVersion[].class);
            return Optional.of(new ArrayList<>(List.of(versions)));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse quilt mappings versions for game version " + gameVersion + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    public static class Entry {
        public QuiltVersion loader = new QuiltVersion();
    }

    @SuppressWarnings("unused")
    public static class QuiltVersion {
        public String separator = "";
        public int build = 0;
        public String maven = "";
        public String version = "";
    }

    @SuppressWarnings("unused")
    public static class QuiltMappingsVersion {
        public String gameVersion = "";
        public String separator = "";
        public int build = 0;
        public String maven = "";
        public String version = "";
        public String hashed = "";
    }
}
