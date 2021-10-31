package ru.pinkgoosik.somikbot.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.api.modrinth.ModVersion;
import ru.pinkgoosik.somikbot.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.somikbot.api.modrinth.ModrinthMod;
import ru.pinkgoosik.somikbot.util.FileUtils;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class ModChangelogPublisher {
    String modSlug;
    String channel;
    String latestVersionId;

    public ModChangelogPublisher(String modSlug, String channel){
        this.modSlug = modSlug;
        this.channel = channel;
        this.latestVersionId = loadCachedData().latestVersionId();
    }

    public void startScheduler(){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(updated()) publish();
                    }
                }, 60 * 1000, 60 * 1000
        );
    }

    private boolean updated(){
        ModrinthMod mod = ModrinthAPI.getModBySlug(modSlug);
        if(mod == null) return false;
        String latest = mod.versions().get(0).id();
        return !latest.isEmpty() && !loadCachedData().latestVersionId().equals(latest);
    }

    private void publish(){
        ModrinthMod mod = ModrinthAPI.getModBySlug(modSlug);
        if(mod == null) return;
        ModVersion modVersion = mod.versions().get(0);
        String changelog = modVersion.changelog();
        Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(mod, modVersion, changelog)).block();
        latestVersionId = modVersion.id();
        saveCachedData();
    }

    private EmbedData createEmbed(ModrinthMod mod, ModVersion version, String latestChangelog){
        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(mod.title()).build())
                .title(version.name())
                .url(mod.modUrl())
                .description("**Changes:**\n" + latestChangelog)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(mod.iconUrl()).build())
                .build();
    }

    private void saveCachedData(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("cache");

            FileWriter writer = new FileWriter("cache/" + modSlug + "_cached.json");
            writer.write(gson.toJson(new CachedData(latestVersionId)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CachedData loadCachedData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cache/" + modSlug + "_cached.json"));
            String id = JsonParser.parseReader(reader).getAsJsonObject().get("latestVersionId").getAsString();
            return new CachedData(id);
        } catch (FileNotFoundException ignored) {}
        return new CachedData("");
    }

    private record CachedData(String latestVersionId){}
}
