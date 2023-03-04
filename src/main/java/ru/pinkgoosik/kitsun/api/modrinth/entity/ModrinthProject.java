package ru.pinkgoosik.kitsun.api.modrinth.entity;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.Nullable;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class ModrinthProject {
	public String id = "";
	public String slug = "";
	@SerializedName("project_type")
	public String projectType = "";
	public String team = "";
	public String title = "";
	public String description = "";
	public String body = "";
	@SerializedName("body_url")
	public String bodyUrl = "";
	public String published = "";
	public String updated = "";
	public String status = "";
	@SerializedName("moderator_message")
	public ModeratorMessage moderatorMessage = new ModeratorMessage();
	public ProjectLicense license = new ProjectLicense();
	@SerializedName("client_side")
	public String clientSide = "";
	@SerializedName("server_side")
	public String serverSide = "";
	public int downloads = 0;
	public int followers = 0;
	public ArrayList<String> categories = new ArrayList<>();
	public ArrayList<String> versions = new ArrayList<>();
	@Nullable
	@SerializedName("icon_url")
	public String iconUrl = "";
	@Nullable
	@SerializedName("issues_url")
	public String issuesUrl = "";
	@Nullable
	@SerializedName("source_url")
	public String sourceUrl = "";
	@Nullable
	@SerializedName("wiki_url")
	public String wikiUrl = "";
	@Nullable
	@SerializedName("discord_url")
	public String discordUrl = "";
	@SerializedName("donation_urls")
	public ArrayList<DonationLink> donationUrls = new ArrayList<>();
	public ArrayList<GalleryEntry> gallery = new ArrayList<>();

	public String getProjectUrl() {
		String url = ModrinthAPI.PROJECT_URL.replace("%slug%", this.slug);
		url = url.replace("%project_type%", this.projectType);
		return url;
	}

	public static class ModeratorMessage {
		public String message = "";
		public String body = "";
	}
}
