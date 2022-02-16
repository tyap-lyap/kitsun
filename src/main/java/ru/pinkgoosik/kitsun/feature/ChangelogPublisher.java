package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedFooterData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.instance.ServerData;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class ChangelogPublisher {
    //Discord server id
    public String serverId;
    //Discord channel id
    public String channel;
    //Modrinth project id
    public String project;
    //Modrinth project's latest version id
    public String latestVersionId = "";

    public ChangelogPublisher(String serverId, String channel, String project) {
        this.serverId = serverId;
        this.channel = channel;
        this.project = project;
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
        return !latest.isEmpty() && !latestVersionId.equals(latest);
    }

    private void publish(ModrinthProject project, ArrayList<ProjectVersion> versions) {
        ProjectVersion modVersion = versions.get(0);
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(project, modVersion)).block();
        latestVersionId = modVersion.id;
        ServerData.get(serverId).saveData();
    }

    private EmbedData createEmbed(ModrinthProject project, ProjectVersion version) {
        String changelogPart = "";

        if(!version.changelog.isBlank() && version.changelog.length() < 5000) {
            changelogPart = changelogPart + "**Changelog**\n" + version.changelog.trim() + "\n ";
        }

        var publisher = ModrinthAPI.getUser(version.author_id);
        if(publisher.isPresent()) {
            if (!changelogPart.isBlank()) changelogPart = changelogPart + "\n";
            String publisherPart = "**Published by** [" + publisher.get().username + "](https://modrinth.com/user/" + publisher.get().id + ")";
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
        String iconUrl;
        if(project.icon_url != null) iconUrl = project.icon_url;
        else iconUrl = "https://raw.githubusercontent.com/PinkGoosik/kitsun/master/img/placeholder_icon.png";

        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(project.title).build())
                .title(version.version_number + " " + versionType + minecraftVersions)
                .url(ModrinthAPI.MOD_URL.replace("%slug%", project.slug))
                .description(changelogPart + linksPart)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(iconUrl).build())
                .footer(EmbedFooterData.builder().text("Modrinth Project | " + project.license.name).iconUrl("https://raw.githubusercontent.com/PinkGoosik/kitsun/master/img/modrinth_logo.png").build())
                .timestamp(Instant.parse(version.date_published).toString())
                .build();
    }
}
