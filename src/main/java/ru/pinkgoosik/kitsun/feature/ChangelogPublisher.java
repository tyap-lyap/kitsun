package ru.pinkgoosik.kitsun.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.modrinth.ModVersion;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthMod;
import ru.pinkgoosik.kitsun.util.FileUtils;

import java.io.*;
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
        if(updated()) publish();
    }

    private boolean updated() {
        Optional<ModrinthMod> mod = ModrinthAPI.getMod(modSlug);
        if(mod.isEmpty()) return false;
        String latest = mod.get().versions().get(0).id();
        return !latest.isEmpty() && !loadCachedData().latestVersionId().equals(latest);
    }

    private void publish() {
        Optional<ModrinthMod> mod = ModrinthAPI.getMod(modSlug);
        if(mod.isEmpty()) return;
        ModVersion modVersion = mod.get().versions().get(0);
        String changelog = modVersion.changelog();
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(mod.get(), modVersion, changelog)).block();
        latestVersionId = modVersion.id();
        saveCachedData();
    }

    private EmbedData createEmbed(ModrinthMod mod, ModVersion version, String latestChangelog) {
        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(mod.title()).build())
                .title(version.name())
                .url(mod.modUrl())
                .description("**Changes:**\n" + latestChangelog)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(mod.iconUrl()).build())
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CachedData loadCachedData() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cache/" + serverID + "/publishers/" + channel + "/" + modSlug + "_cached.json"));
            String id = JsonParser.parseReader(reader).getAsJsonObject().get("latestVersionId").getAsString();
            return new CachedData(id);
        } catch (FileNotFoundException ignored) {}
        return new CachedData("");
    }

    private record CachedData(String latestVersionId){}
}
