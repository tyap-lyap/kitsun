package ru.pinkgoosik.kitsun.api.modrinth.entity;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ProjectVersion {
    public String id = "";
    public String project_id = "";
    public String author_id = "";
    public boolean featured = false;
    public String name = "";
    public String version_number = "";
    public String changelog = "";
    public String changelog_url = "";
    public String date_published = "";
    public int downloads = 0;
    public String version_type = "";
    public ArrayList<ProjectFile> files = new ArrayList<>();
    public ArrayList<DependencyEntry> dependencies = new ArrayList<>();
    public ArrayList<String> game_versions = new ArrayList<>();
    public ArrayList<String> loaders = new ArrayList<>();
}
