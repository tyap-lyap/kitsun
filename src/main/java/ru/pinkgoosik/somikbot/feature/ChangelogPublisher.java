package ru.pinkgoosik.somikbot.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.RestClient;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.api.ModVersion;
import ru.pinkgoosik.somikbot.api.ModrinthAPI;
import ru.pinkgoosik.somikbot.api.ModrinthMod;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

public class ChangelogPublisher {
    String modSlug;
    long delay = 60;
    String channel = "896632013556162580";
    RestClient client;
    String latestVersionId;

    public ChangelogPublisher(RestClient client, String modSlug){
        this.client = client;
        this.modSlug = modSlug;
        this.latestVersionId = loadCachedData();
        this.startScheduler();
    }

    private void startScheduler(){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if(updated()) publish();
                    }
                }, delay * 1000, delay * 1000
        );
    }

    private boolean updated(){
        ModrinthMod mod = ModrinthAPI.getModBySlug(modSlug);
        String latest = mod.versions.get(0).id;
        return !latest.isEmpty() && !latestVersionId.equals(latest);
    }

    private void publish(){
        ModrinthMod mod = ModrinthAPI.getModBySlug(modSlug);
        ModVersion modVersion = mod.versions.get(0);
        String changelog = modVersion.changelog;
        client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(mod, modVersion, changelog)).block();
        latestVersionId = modVersion.id;
        saveCachedData();
    }

    private EmbedData createEmbed(ModrinthMod mod, ModVersion version, String latestChangelog){
        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(mod.title).build())
                .title(version.name)
                .url(mod.modUrl)
                .description("**Changes:**\n" + latestChangelog)
                .color(Color.of(48,178,123).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(mod.iconUrl).build())
                .build();
    }

    private void saveCachedData(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter(modSlug + "_cached.json");
            writer.write(gson.toJson(new CachedData(latestVersionId)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String loadCachedData(){
        try {
            String id;
            BufferedReader reader = new BufferedReader(new FileReader(modSlug + "_cached.json"));
            id = JsonParser.parseReader(reader).getAsJsonObject().get("latestVersionId").getAsString();
            return id;
        } catch (FileNotFoundException ignored) {}
        return "";
    }

    private record CachedData(String latestVersionId){}
}
