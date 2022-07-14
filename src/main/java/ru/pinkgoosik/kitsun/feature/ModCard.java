package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.*;
import discord4j.rest.entity.RestMessage;
import discord4j.rest.util.Color;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.curseforge.CurseForgeAPI;
import ru.pinkgoosik.kitsun.api.curseforge.entity.CurseForgeMod;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;

import java.time.Instant;
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

    public boolean shouldBeRemoved = false;

    public ModCard(String serverId, CurseForgeMod mod, ModrinthProject project, String channelId, String messageId) {
        this.server = serverId;
        this.curseforge = mod.data.getStringId();
        this.curseforgeSlug = mod.data.slug;
        this.modrinth = project.id;
        this.modrinthSlug = project.slug;
        this.channel = channelId;
        this.message = messageId;
    }

    public void update() {
        var message = this.getMessage();
        var mod = CurseForgeAPI.getMod(this.curseforge);
        var project = ModrinthAPI.getProject(this.modrinth);

        if(message.isPresent()) {
            if(project.isPresent() && mod.isPresent()) {
                //slugs can be changed anytime
                this.curseforgeSlug = mod.get().data.slug;
                this.modrinthSlug = project.get().slug;

                try {
                    message.get().edit(MessageEditRequest.builder().embed(this.createEmbed(project.get(), mod.get())).build()).block();
                }
                catch (Exception e) {
                    KitsunDebugger.report("Failed to update " + this.modrinthSlug + " card's message due to an exception:\n" + e);
                    if(e.getMessage().contains("Unknown Message")) {
                        this.shouldBeRemoved = true;
                    }
                }
            }
        }
        else {
            this.shouldBeRemoved = true;
        }

    }

    public Optional<RestMessage> getMessage() {
        try {
            return Optional.of(Bot.rest.getChannelById(Snowflake.of(this.channel)).getRestMessage(Snowflake.of(this.message)));
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to get " + this.modrinthSlug + " card's message due to an exception:\n" + e);
            return Optional.empty();
        }
    }

    public EmbedData createEmbed(ModrinthProject project, CurseForgeMod mod) {
        int downloads = project.downloads + mod.data.downloadCount;
        String curseforgeLink = mod.data.links.websiteUrl;
        String modrinthLink = project.getProjectUrl();
        String iconUrl = project.icon_url != null ? project.icon_url : "https://i.imgur.com/rM5bzkK.png";
        String description = project.description;

        String statsPart = "\n**Statistics**\n Downloads: **" + commas(downloads) + "** | Followers: **" + commas(project.followers) + "**";

        String linksPart = "\n**Resources**";
        linksPart = linksPart + "\n[CurseForge](" + curseforgeLink + ")";
        linksPart = linksPart + " | [Modrinth](" + modrinthLink + ")";

        if (project.source_url != null) {
            linksPart = linksPart + " | [Source](" + project.source_url + ")";
        }
        if (project.issues_url != null) {
            linksPart = linksPart + " | [Issues](" + project.issues_url + ")";
        }
        if (project.wiki_url != null) {
            linksPart = linksPart + " | [Wiki](" + project.wiki_url + ")";
        }

        String mcVersion = "";
        var versions = ModrinthAPI.getVersions(project.slug);
        if(versions.isPresent()) {
            mcVersion = " for " + versions.get().get(0).game_versions.get(0);
            var first = versions.get().get(versions.get().size() - 1).game_versions.get(0);

            if(!first.equals(versions.get().get(0).game_versions.get(0))) {
                mcVersion = " for " + first + " - " + versions.get().get(0).game_versions.get(0);
            }
        }

        return EmbedData.builder()
            .title(project.title + mcVersion)
            .description(description + statsPart + linksPart)
            .color(Color.of(48,178,123).getRGB())
            .thumbnail(EmbedThumbnailData.builder().url(iconUrl).build())
            .footer(EmbedFooterData.builder().text("Minecraft Mod | " + project.license.name).build())
            .timestamp(Instant.parse(project.published).toString())
            .build();
    }

    public String commas(int value) {
        String num = Integer.toString(value);

        String result = "";
        for(int i = 0; i < num.length() ; i++) {
            if((num.length() - i - 1) % 3 == 0) {
                result += num.charAt(i) + ",";
            }
            else {
                result += Character.toString(num.charAt(i));
            }
        }
        return result;
    }
}
