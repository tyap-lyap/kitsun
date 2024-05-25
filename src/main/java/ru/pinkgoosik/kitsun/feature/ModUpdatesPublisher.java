package ru.pinkgoosik.kitsun.feature;

import masecla.modrinth4j.model.project.Project;
import masecla.modrinth4j.model.user.ModrinthUser;
import masecla.modrinth4j.model.version.ProjectVersion;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.Modrinth;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
	public boolean manualCall = false;

	public transient Project cachedProject = null;

	public ModUpdatesPublisher(String serverID, String channelID, String projectID, boolean manualCall) {
		this.server = serverID;
		this.channel = channelID;
		this.project = projectID;
		this.manualCall = manualCall;
	}

	public void check(long delay) {
		if(cachedProject == null) {
			Modrinth.getProject(this.project).ifPresent(proj -> this.cachedProject = proj);
		}
		Optional<ArrayList<ProjectVersion>> versions = Modrinth.getVersions(this.project);
		if(versions.isPresent()) {
			if(updated(versions.get())) publish(versions.get(), delay);
		}
	}

	private boolean updated(ArrayList<ProjectVersion> versions) {
		String latest = versions.get(0).getId();
		return !latest.isEmpty() && !latestVersion.equals(latest);
	}

	private void publish(ArrayList<ProjectVersion> versions, long delay) {
		ProjectVersion modVersion = versions.get(0);
		if(Bot.jda.getGuildChannelById(channel) instanceof StandardGuildMessageChannel messageChannel) {
			messageChannel.sendMessageEmbeds(createEmbed(this.cachedProject, modVersion)).queueAfter(delay, TimeUnit.SECONDS, message -> {},throwable -> {
				KitsunDebugger.ping("Failed to send update message of the " + this.project + " project due to an exception:\n" + throwable);
			});
		}
		latestVersion = modVersion.getId();
		ServerData.get(server).save();
	}

	public static MessageEmbed createEmbed(Project project, ProjectVersion version) {
		String changelogPart = "";

		if(!version.getChangelog().isBlank() && version.getChangelog().length() < 5000) {
			changelogPart = changelogPart + "**Changelog**\n" + version.getChangelog().trim() + "\n ";
		}

		Optional<ModrinthUser> user = Modrinth.getUser(version.getAuthorId());
		if(user.isPresent()) {
			if(!changelogPart.isBlank()) changelogPart = changelogPart + "\n";
			String publisherPart = "**Published by** [" + user.get().getUsername() + "](https://modrinth.com/user/" + user.get().getId() + ")";
			changelogPart = changelogPart + publisherPart;
		}

		String linksPart = "";
		if(project.getSourceUrl() != null) {
			linksPart = linksPart + "\n[Source Code](" + project.getSourceUrl() + ")";
		}
		if(project.getIssuesUrl() != null) {
			if(!linksPart.isBlank()) linksPart = linksPart + " | ";
			else linksPart = linksPart + "\n";
			linksPart = linksPart + "[Issue Tracker](" + project.getIssuesUrl() + ")";
		}
		if(project.getWikiUrl() != null) {
			if(!linksPart.isBlank()) linksPart = linksPart + " | ";
			else linksPart = linksPart + "\n";
			linksPart = linksPart + "[Wiki](" + project.getWikiUrl() + ")";
		}
		String versionType = version.getVersionType().name().toLowerCase().substring(0, 1).toUpperCase() + version.getVersionType().name().toLowerCase().substring(1);
		String minecraftVersions = " for " + version.getGameVersions().get(0);

		if(version.getGameVersions().size() > 1) {
			minecraftVersions = minecraftVersions + " - " + version.getGameVersions().get(version.getGameVersions().size() - 1);
		}
		String iconUrl = project.getIconUrl() != null ? project.getIconUrl() : "https://i.imgur.com/rM5bzkK.png";
		String versionNum = version.getVersionNumber();

		return new EmbedBuilder()
				.setAuthor(project.getTitle())
				.setTitle((versionNum.contains("+") ? versionNum.split("\\+")[0] : versionNum) + " " + versionType + minecraftVersions, Modrinth.getUrl(project))
				.setDescription(changelogPart + linksPart)
				.setColor(KitsunColors.getCyan().getRGB())
				.setThumbnail(iconUrl)
				.setFooter("Modrinth Project | " + project.getLicense().getName(), "https://cdn.discordapp.com/emojis/1040805093395673128.png")
				.setTimestamp(version.getDatePublished())
				.build();
	}
}
