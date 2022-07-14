package ru.pinkgoosik.kitsun.api.modrinth.entity;

import org.checkerframework.checker.nullness.qual.Nullable;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;

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
    @Nullable
    public String icon_url = "";
    @Nullable
    public String issues_url = "";
    @Nullable
    public String source_url = "";
    @Nullable
    public String wiki_url = "";
    @Nullable
    public String discord_url = "";
    public ArrayList<DonationLink> donation_urls = new ArrayList<>();
    public ArrayList<GalleryEntry> gallery = new ArrayList<>();

    public String getProjectUrl() {
        String url = ModrinthAPI.MOD_URL.replace("%slug%", this.slug);
        url = url.replace("mod", this.project_type);
        return url;
    }

    public static class ModeratorMessage {
        public String message = "";
        public String body = "";
    }
}
