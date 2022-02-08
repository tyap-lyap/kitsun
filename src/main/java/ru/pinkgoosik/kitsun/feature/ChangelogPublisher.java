package ru.pinkgoosik.kitsun.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
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
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

public class ChangelogPublisher {
    public final String modSlug;
    public final String channel;
    private String latestVersionId;
    public final String serverID;

    public ChangelogPublisher(String modSlug, String channel, String serverID) {
        this.modSlug = modSlug;
        this.channel = channel;
        this.serverID = serverID;
        this.latestVersionId = loadCachedData().latestVersionId();
    }

    public void check() {
        Optional<ModrinthProject> mod = ModrinthAPI.getProject(modSlug);
        Optional<ArrayList<ProjectVersion>> versions = ModrinthAPI.getVersions(modSlug);
        if(mod.isPresent() && versions.isPresent()) {
            if(updated(versions.get())) publish(mod.get(), versions.get());
        }
    }

    private boolean updated(ArrayList<ProjectVersion> versions) {
        String latest = versions.get(0).id;
        return !latest.isEmpty() && !loadCachedData().latestVersionId().equals(latest);
    }

    private void publish(ModrinthProject mod, ArrayList<ProjectVersion> versions) {
        ProjectVersion modVersion = versions.get(0);
        String changelog = modVersion.changelog;
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(mod, modVersion, changelog)).block();
        latestVersionId = modVersion.id;
        saveCachedData();
    }

    private EmbedData createEmbed(ModrinthProject mod, ProjectVersion version, String latestChangelog) {
        String changelogPart = "**Changelog**\n" + latestChangelog;

        var publisher = ModrinthAPI.getUser(version.author_id);
        if(publisher.isPresent()) {
            String publisherPart = "\n**Published by** [" + publisher.get().username + "](https://modrinth.com/user/" + publisher.get().id + ")";
            changelogPart = changelogPart + publisherPart;
        }

        String linksPart = "";
        if (mod.source_url != null) {
            linksPart = linksPart + "\n[Source Code](" + mod.source_url + ")";
        }
        if (mod.issues_url != null) {
            if (!linksPart.isBlank()) linksPart = linksPart + " | ";
            linksPart = linksPart + "[Issue Tracker](" + mod.issues_url + ")";
        }
        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(mod.title).build())
                .title(version.name)
                .url(ModrinthAPI.MOD_URL.replace("%slug%", mod.slug))
                .description(changelogPart + linksPart)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(mod.icon_url).build())
                .footer(EmbedFooterData.builder().text("Modrinth Project | " + mod.license.name).build())
                .timestamp(Instant.parse(version.date_published).toString())
                .build();
    }

    private void saveCachedData() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("cache/" + serverID + "/publishers/" + channel);

            FileWriter writer = new FileWriter("cache/" + serverID + "/publishers/" + channel + "/" + modSlug + "_cached.json");
            writer.write(gson.toJson(new CachedData(latestVersionId)));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CachedData loadCachedData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cache/" + serverID + "/publishers/" + channel + "/" + modSlug + "_cached.json"));
            String id = JsonParser.parseReader(reader).getAsJsonObject().get("latestVersionId").getAsString();
            return new CachedData(id);
        } catch (Exception ignored) {}
        return new CachedData("");
    }

    private record CachedData(String latestVersionId){}
}
