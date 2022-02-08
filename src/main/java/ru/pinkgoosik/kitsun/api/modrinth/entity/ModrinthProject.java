package ru.pinkgoosik.kitsun.api.modrinth.entity;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ModrinthProject {
    public String id = "";
    public String slug = "";
    public String project_type = "";
    public String team = "";
    public String title = "";
    public String description = "";
    public String body = "";
    public String body_url = "";
    public String published = "";
    public String updated = "";
    public String status = "";
    public ModeratorMessage moderator_message = new ModeratorMessage();
    public ProjectLicense license = new ProjectLicense();
    public String client_side = "";
    public String server_side = "";
    public int downloads = 0;
    public int followers = 0;
    public ArrayList<String> categories = new ArrayList<>();
    public ArrayList<String> versions = new ArrayList<>();
    public String icon_url = "";
    public String issues_url = "";
    public String source_url = "";
    public String wiki_url = "";
    public String discord_url = "";
    public ArrayList<String> donation_urls = new ArrayList<>();
    public ArrayList<GalleryEntry> gallery = new ArrayList<>();

    public static class ModeratorMessage {
        public String message = "";
        public String body = "";
    }
}
