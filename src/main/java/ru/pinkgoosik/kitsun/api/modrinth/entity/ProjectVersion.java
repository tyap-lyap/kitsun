package ru.pinkgoosik.kitsun.api.modrinth.entity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ProjectVersion {
    public String id = "";
    @SerializedName("project_id")
    public String projectId = "";
    @SerializedName("author_id")
    public String authorId = "";
    public boolean featured = false;
    public String name = "";
    @SerializedName("version_number")
    public String versionNumber = "";
    public String changelog = "";
    @SerializedName("changelog_url")
    public String changelogUrl = "";
    @SerializedName("date_published")
    public String datePublished = "";
    public int downloads = 0;
    @SerializedName("version_type")
    public String versionType = "";
    public ArrayList<ProjectFile> files = new ArrayList<>();
    public ArrayList<DependencyEntry> dependencies = new ArrayList<>();
    @SerializedName("game_versions")
    public ArrayList<String> gameVersions = new ArrayList<>();
    public ArrayList<String> loaders = new ArrayList<>();
}
