package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.*;
import discord4j.rest.entity.RestMessage;
import org.checkerframework.checker.nullness.qual.Nullable;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.curseforge.CurseForgeAPI;
import ru.pinkgoosik.kitsun.api.curseforge.entity.CurseForgeMod;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ModCard {
	//Discord server's id
	public String server;
	//Discord channel's id
	public String channel;
	//Discord message's id
	public String message;
	public String modrinth;
	public String modrinthSlug;
	public String curseforge;
	public String curseforgeSlug;
	public boolean hasCurseforgePage = false;
	public boolean hasModrinthPage = false;

	public boolean shouldBeRemoved = false;

	public ModCard(String serverId, @Nullable CurseForgeMod mod, @Nullable ModrinthProject project, String channelId, String messageId) {
		this.server = serverId;
		if(mod != null) {
			this.curseforge = mod.data.getStringId();
			this.curseforgeSlug = mod.data.slug;
			this.hasCurseforgePage = true;
		}
		if(project != null) {
			this.modrinth = project.id;
			this.modrinthSlug = project.slug;
			this.hasModrinthPage = true;
		}
		this.channel = channelId;
		this.message = messageId;
	}

	public void update() {
		var message = this.getMessage();
		if(message.isPresent()) {
			if(this.hasCurseforgePage && this.hasModrinthPage) {
				var project = ModrinthAPI.getProject(this.modrinth);
				var mod = CurseForgeAPI.getMod(this.curseforge);
				if(mod.isPresent() && project.isPresent()) {
					//slugs can be changed anytime
					this.curseforgeSlug = mod.get().data.slug;
					this.modrinthSlug = project.get().slug;
					this.updateMessage(message.get(), project.get(), mod.get());
				}
			}
			else if(this.hasModrinthPage) {
				var project = ModrinthAPI.getProject(this.modrinth);
				if(project.isPresent()) {
					this.modrinthSlug = project.get().slug;
					this.updateMessage(message.get(), project.get(), null);
				}
			}
			else if(this.hasCurseforgePage) {
				var mod = CurseForgeAPI.getMod(this.curseforge);
				if(mod.isPresent()) {
					this.curseforgeSlug = mod.get().data.slug;
					this.updateMessage(message.get(), null, mod.get());
				}
			}
		}
	}

	private void updateMessage(RestMessage message, ModrinthProject project, @Nullable CurseForgeMod mod) {
		try {
			message.edit(MessageEditRequest.builder().embedOrNull(this.createEmbed(project, mod)).build()).block();
		}
		catch(Exception e) {
			if(e.getMessage().contains("Unknown Message")) {
				this.shouldBeRemoved = true;
			}
			else {
				KitsunDebugger.report("Failed to update " + this.modrinthSlug + " card's message due to an exception:\n" + e);
			}
		}
	}

	public Optional<RestMessage> getMessage() {
		try {
			return Optional.of(Bot.rest.getChannelById(Snowflake.of(this.channel)).getRestMessage(Snowflake.of(this.message)));
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to get " + this.modrinthSlug + " card's message due to an exception:\n" + e);
			return Optional.empty();
		}
	}

	public EmbedData createEmbed(@Nullable ModrinthProject project, @Nullable CurseForgeMod mod) {
		int downloads = 0;
		if(project != null) downloads = downloads + project.downloads;
		if(mod != null) downloads = downloads + mod.data.downloadCount;

		String modrinthLink = project != null ? project.getProjectUrl() : "";

		String iconUrl = "";

		if(project != null) {
			iconUrl = project.iconUrl != null ? project.iconUrl : "https://i.imgur.com/rM5bzkK.png";
		}
		else if(mod != null) {
			iconUrl = mod.data.links.websiteUrl;
		}

		String description = "";

		if(project != null) description = compact(project.description);
		if(mod != null) description = compact(mod.data.summary);

		String stats = "Downloads: **" + commas(downloads) + "**";
		if(project != null) stats = stats + " | Followers: **" + commas(project.followers) + "**";

		if(project != null) {
			Instant updated = Instant.parse(project.updated);
			Instant created = Instant.parse(project.published);
			Instant now = Instant.now();

			stats = stats + "\nUpdated: **" + commas((int) ChronoUnit.DAYS.between(updated, now)) + "** days ago";
			stats = stats + " | Created: **" + commas((int) ChronoUnit.DAYS.between(created, now)) + "** days ago";
		}


		String links = "";

		if(project != null) {
			links = links + "[Modrinth](" + modrinthLink + ")";
		}
		else if(mod != null) {
			links = links + "[CurseForge](" + mod.data.links.websiteUrl + ")";
		}

		if(project != null && mod != null) {
			links = links + " | [CurseForge](" + mod.data.links.websiteUrl + ")";
		}

		if(project != null && project.sourceUrl != null) {
			links = links + " | [Source](" + project.sourceUrl + ")";
		}
		else if(mod != null && mod.data.links.sourceUrl != null && !mod.data.links.sourceUrl.isBlank()) {
			links = links + " | [Source](" + mod.data.links.sourceUrl + ")";
		}
		if(project != null && project.issuesUrl != null) {
			links = links + " | [Issues](" + project.issuesUrl + ")";
		}
		else if(mod != null && mod.data.links.issuesUrl != null && !mod.data.links.issuesUrl.isBlank()) {
			links = links + " | [Issues](" + mod.data.links.issuesUrl + ")";
		}
		if(project != null && project.wikiUrl != null) {
			links = links + " | [Wiki](" + project.wikiUrl + ")";
		}
		else if(mod != null && mod.data.links.wikiUrl != null && !mod.data.links.wikiUrl.isBlank()) {
			links = links + " | [Wiki](" + mod.data.links.wikiUrl + ")";
		}

		String mcVersion = "";
		if(project != null) {
			var versions = ModrinthAPI.getVersions(project.slug);
			if(versions.isPresent()) {
				mcVersion = " for " + versions.get().get(0).gameVersions.get(0);
				var first = versions.get().get(versions.get().size() - 1).gameVersions.get(0);

				if(!first.equals(versions.get().get(0).gameVersions.get(0))) {
					mcVersion = " for " + first + " - " + versions.get().get(0).gameVersions.get(0);
				}
			}
		}

		String title = "";

		if(project != null) {
			title = project.title;
		}
		else if(mod != null) {
			title = mod.data.name;
		}

		String license = "";
		Instant instant = Instant.now();

		if(project != null) {
			license = " | " + project.license.name;
			instant = Instant.parse(project.published);
		}

		return EmbedData.builder()
				.title(title + mcVersion)
				.description(description)
				.addField(EmbedFieldData.builder().name("Statistics").value(stats).inline(false).build())
				.addField(EmbedFieldData.builder().name("Resources").value(links).inline(false).build())
				.color(KitsunColors.getCyan().getRGB())
				.thumbnail(EmbedThumbnailData.builder().url(iconUrl).build())
				.footer(EmbedFooterData.builder().text("Minecraft Mod" + license).build())
				.timestamp(instant.toString())
				.build();
	}

	public String commas(int value) {
		String num = Integer.toString(value);

		StringBuilder result = new StringBuilder();
		for(int i = 0; i < num.length(); i++) {
			if((num.length() - i - 1) % 3 == 0) {
				result.append(num.charAt(i)).append(",");
			}
			else {
				result.append(num.charAt(i));
			}
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

	public String compact(String text) {
		String[] words = text.split(" ");

		StringBuilder line = new StringBuilder();
		StringBuilder result = new StringBuilder();

		for(String word : words) {
			if(line.length() + word.length() > 36) {
				result.append(line).append("\n");
				line = new StringBuilder();
			}
			line.append(word).append(" ");
		}
		result.append(line);
		return result.toString();
	}

}
