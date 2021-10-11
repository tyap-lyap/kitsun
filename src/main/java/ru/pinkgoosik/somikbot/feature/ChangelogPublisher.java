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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChangelogPublisher {

    String modSlug;
    long delayInSec = 60;
    long channelId = 896632013556162580L;
    RestClient client;
    ArrayList<String> cachedVersionIds;

    public ChangelogPublisher(RestClient client, String modSlug){
        this.client = client;
        this.modSlug = modSlug;
        this.cachedVersionIds = loadCachedFileIds();
        this.startScheduler();
    }

    void startScheduler(){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        createMessage();
                    }
                },
                delayInSec * 1000, delayInSec * 1000
        );
    }

    void createMessage(){
        ModrinthMod modrinthMod = ModrinthAPI.getModBySlug(modSlug);
        ArrayList<String> versionIds = new ArrayList<>();
        String latestChangelog = "";
        ModVersion latestVersion = new ModVersion();

        modrinthMod.versions.forEach(version -> versionIds.add(version.id));
        for(ModVersion modVersion : modrinthMod.versions){
            if(versionIds.get(0).equals(modVersion.id)){
                latestVersion = modVersion;
                latestChangelog = modVersion.changelog;
            }
        }
        if(!cachedVersionIds.equals(versionIds)){
            if(!versionIds.isEmpty()){
                client.getChannelById(Snowflake.of(channelId)).createMessage(createEmbed(modrinthMod, latestVersion, latestChangelog)).block();
                cachedVersionIds = versionIds;
            }
            saveCachedFileIds();
        }
    }

    private EmbedData createEmbed(ModrinthMod mod, ModVersion version, String latestChangelog){
        return EmbedData.builder()
                .author(EmbedAuthorData.builder().name(mod.title).build())
                .title(version.name)
                .url(mod.modUrl)
                .description("**Changes:**\n" + latestChangelog.replaceAll("\\*", "-"))
                .color(Color.GREEN.getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(mod.iconUrl).build())
                .build();
    }

    private void saveCachedFileIds(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter(modSlug + "_cached_ids.json");
            writer.write(gson.toJson(cachedVersionIds));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> loadCachedFileIds(){
        try {
            ArrayList<String> ids = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(modSlug + "_cached_ids.json"));
            JsonParser.parseReader(reader).getAsJsonArray().forEach(jsonElement -> ids.add(jsonElement.getAsString()));
            return ids;
        } catch (FileNotFoundException ignored) {}
        return new ArrayList<>();
    }
}
