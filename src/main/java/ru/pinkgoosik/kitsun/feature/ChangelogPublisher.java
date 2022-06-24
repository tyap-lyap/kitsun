package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFooterData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthUser;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.cache.ServerData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class ChangelogPublisher {
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

    public ChangelogPublisher(String serverID, String channelID, String projectID) {
        this.server = serverID;
        this.channel = channelID;
        this.project = projectID;
    }

    public void check() {
        Optional<ModrinthProject> project = ModrinthAPI.getProject(this.project);
        Optional<ArrayList<ProjectVersion>> versions = ModrinthAPI.getVersions(this.project);
        if(project.isPresent() && versions.isPresent()) {
            if(updated(versions.get())) publish(project.get(), versions.get());
        }
    }

    private boolean updated(ArrayList<ProjectVersion> versions) {
        String latest = versions.get(0).id;
        return !latest.isEmpty() && !latestVersion.equals(latest);
    }

    private void publish(ModrinthProject project, ArrayList<ProjectVersion> versions) {
        ProjectVersion modVersion = versions.get(0);
        Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(project, modVersion)).block();
        latestVersion = modVersion.id;
        ServerData.get(server).save();
    }

    private EmbedData createEmbed(ModrinthProject project, ProjectVersion version) {
        String changelogPart = "";

        if(!version.changelog.isBlank() && version.changelog.length() < 5000) {
            changelogPart = changelogPart + "**Changelog**\n" + version.changelog.trim() + "\n ";
        }

        Optional<ModrinthUser> user = ModrinthAPI.getUser(version.author_id);
        if(user.isPresent()) {
            if (!changelogPart.isBlank()) changelogPart = changelogPart + "\n";
            String publisherPart = "**Published by** [" + user.get().username + "](https://modrinth.com/user/" + user.get().id + ")";
            changelogPart = changelogPart + publisherPart;
        }

        String linksPart = "";
        if (project.source_url != null) {
            linksPart = linksPart + "\n[Source Code](" + project.source_url + ")";
        }
        if (project.issues_url != null) {
            if (!linksPart.isBlank()) linksPart = linksPart + " | ";
            else linksPart = linksPart + "\n";
            linksPart = linksPart + "[Issue Tracker](" + project.issues_url + ")";
        }
        if (project.wiki_url != null) {
            if (!linksPart.isBlank()) linksPart = linksPart + " | ";
            else linksPart = linksPart + "\n";
            linksPart = linksPart + "[Wiki](" + project.wiki_url + ")";
        }
        String versionType = version.version_type.substring(0, 1).toUpperCase() + version.version_type.substring(1);
        String minecraftVersions = " for " + version.game_versions.get(0);

        if(version.game_versions.size() > 1) {
            minecraftVersions = minecraftVersions + " - " + version.game_versions.get(version.game_versions.size() - 1);
        }
        String iconUrl = project.icon_url != null ? project.icon_url : "https://i.imgur.com/rM5bzkK.png";

        //I hate qsl icon lmao
        if(project.slug.equals("qsl")) {
            iconUrl = "https://github.com/QuiltMC/art/blob/master/brand/512png/quilt_mini_icon_dark.png?raw=true";
        }
        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(project.title).build())
                .title(version.version_number + " " + versionType + minecraftVersions)
                .url(ModrinthAPI.MOD_URL.replace("%slug%", project.slug))
                .description(changelogPart + linksPart)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(iconUrl).build())
                .footer(EmbedFooterData.builder().text("Modrinth Project | " + project.license.name).iconUrl("https://i.imgur.com/abiIc1b.png").build())
                .timestamp(Instant.parse(version.date_published).toString())
                .build();
    }
}
