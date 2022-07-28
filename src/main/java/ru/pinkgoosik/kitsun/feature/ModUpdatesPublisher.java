package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFooterData;
import discord4j.discordjson.json.EmbedThumbnailData;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthUser;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class ModUpdatesPublisher {
	/**
	 * Discord server ID that this ChangelogPublisher belongs to
	 */
	public String server;
	/**
	 * Discord server's channel ID for this ChangelogPublisher publish to
	 */
	public String channel;
	/**
	 * Modrinth project ID for this ChangelogPublisher publish for
	 */
	public String project;
	/**
	 * Cached Modrinth project's latest version ID
	 */
	public String latestVersion = "";

	public transient ModrinthProject cachedProject = null;

	public ModUpdatesPublisher(String serverID, String channelID, String projectID) {
		this.server = serverID;
		this.channel = channelID;
		this.project = projectID;
	}

	public void check() {
		if(cachedProject == null) {
			ModrinthAPI.getProject(this.project).ifPresent(proj -> this.cachedProject = proj);
		}
		Optional<ArrayList<ProjectVersion>> versions = ModrinthAPI.getVersions(this.project);
		if(versions.isPresent()) {
			if(updated(versions.get())) publish(versions.get());
		}
	}

	private boolean updated(ArrayList<ProjectVersion> versions) {
		String latest = versions.get(0).id;
		return !latest.isEmpty() && !latestVersion.equals(latest);
	}

	private void publish(ArrayList<ProjectVersion> versions) {
		ProjectVersion modVersion = versions.get(0);
		Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(modVersion)).block();
		latestVersion = modVersion.id;
		ServerData.get(server).save();
	}

	private EmbedData createEmbed(ProjectVersion version) {
		String changelogPart = "";

		if(!version.changelog.isBlank() && version.changelog.length() < 5000) {
			changelogPart = changelogPart + "**Changelog**\n" + version.changelog.trim() + "\n ";
		}

		Optional<ModrinthUser> user = ModrinthAPI.getUser(version.authorId);
		if(user.isPresent()) {
			if(!changelogPart.isBlank()) changelogPart = changelogPart + "\n";
			String publisherPart = "**Published by** [" + user.get().username + "](https://modrinth.com/user/" + user.get().id + ")";
			changelogPart = changelogPart + publisherPart;
		}

		String linksPart = "";
		if(cachedProject.sourceUrl != null) {
			linksPart = linksPart + "\n[Source Code](" + cachedProject.sourceUrl + ")";
		}
		if(cachedProject.issuesUrl != null) {
			if(!linksPart.isBlank()) linksPart = linksPart + " | ";
			else linksPart = linksPart + "\n";
			linksPart = linksPart + "[Issue Tracker](" + cachedProject.issuesUrl + ")";
		}
		if(cachedProject.wikiUrl != null) {
			if(!linksPart.isBlank()) linksPart = linksPart + " | ";
			else linksPart = linksPart + "\n";
			linksPart = linksPart + "[Wiki](" + cachedProject.wikiUrl + ")";
		}
		String versionType = version.versionType.substring(0, 1).toUpperCase() + version.versionType.substring(1);
		String minecraftVersions = " for " + version.gameVersions.get(0);

		if(version.gameVersions.size() > 1) {
			minecraftVersions = minecraftVersions + " - " + version.gameVersions.get(version.gameVersions.size() - 1);
		}
		String iconUrl = cachedProject.iconUrl != null ? cachedProject.iconUrl : "https://i.imgur.com/rM5bzkK.png";

		//I hate qsl icon lmao
		if(cachedProject.slug.equals("qsl")) {
			iconUrl = "https://github.com/QuiltMC/art/blob/master/brand/512png/quilt_mini_icon_dark.png?raw=true";
		}
		return EmbedData.builder()
				.author(EmbedAuthorData.builder().name(cachedProject.title).build())
				.title(version.versionNumber + " " + versionType + minecraftVersions)
				.url(cachedProject.getProjectUrl())
				.description(changelogPart + linksPart)
				.color(KitsunColors.getCyan().getRGB())
				.thumbnail(EmbedThumbnailData.builder().url(iconUrl).build())
				.footer(EmbedFooterData.builder().text("Modrinth Project | " + cachedProject.license.name).iconUrl("https://i.imgur.com/abiIc1b.png").build())
				.timestamp(Instant.parse(version.datePublished).toString())
				.build();
	}
}
