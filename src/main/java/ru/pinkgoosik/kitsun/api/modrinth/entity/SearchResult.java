package ru.pinkgoosik.kitsun.api.modrinth.entity;

import com.google.gson.annotations.SerializedName;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class SearchResult {
    public ArrayList<Hit> hits = new ArrayList<>();
    public int offset = 0;
    public int limit = 0;

    @SerializedName("total_hits")
    public int totalHits = 0;

    public static class Hit {
        public String slug = "";
        public String title = "";
        public String description = "";
        public ArrayList<String> categories = new ArrayList<>();
        @SerializedName("client_side")
        public String clientSide = "";
        @SerializedName("server_side")
        public String serverSide = "";
        @SerializedName("project_type")
        public String projectType = "";
        public String downloads = "";
        @Nullable
        @SerializedName("icon_url")
        public String iconUrl = "";
        @SerializedName("project_id")
        public String projectId = "";
        public String author = "";
        public ArrayList<String> versions = new ArrayList<>();
        public int follows = 0;
        @SerializedName("date_created")
        public String dateCreated = "";
        @SerializedName("date_modified")
        public String dateModified = "";
        @SerializedName("latest_version")
        public String latestVersion = "";
        public String license = "";
        public ArrayList<String> gallery = new ArrayList<>();
    }
}
