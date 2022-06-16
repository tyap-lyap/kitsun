package ru.pinkgoosik.kitsun.api.modrinth.entity;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class SearchResult {
    public ArrayList<Hit> hits = new ArrayList<>();
    public int offset = 0;
    public int limit = 0;
    public int total_hits = 0;

    public static class Hit {
        public String slug = "";
        public String title = "";
        public String description = "";
        public ArrayList<String> categories = new ArrayList<>();
        public String client_side = "";
        public String server_side = "";
        public String project_type = "";
        public String downloads = "";
        @Nullable
        public String icon_url = "";
        public String project_id = "";
        public String author = "";
        public ArrayList<String> versions = new ArrayList<>();
        public int follows = 0;
        public String date_created = "";
        public String date_modified = "";
        public String latest_version = "";
        public String license = "";
        public ArrayList<String> gallery = new ArrayList<>();
    }
}
