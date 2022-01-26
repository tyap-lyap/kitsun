package ru.pinkgoosik.kitsun.feature;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.MinecraftVersions;
import ru.pinkgoosik.kitsun.util.FileUtils;
import ru.pinkgoosik.kitsun.util.GlobalColors;

import java.io.*;

public class MCUpdatesPublisher {
    private static final String QUILT_PATCH_NOTES = "https://quiltmc.org/mc-patchnotes/#%version%";
    public final String channel = "900427004069957652";
    private String latestRelease;
    private String latestSnapshot;

    public MCUpdatesPublisher(){
        CachedData data = loadCachedData();
        this.latestRelease = data.latestRelease();
        this.latestSnapshot = data.latestSnapshot();
    }

    public void check(){
        checkForReleaseUpdates();
        checkForSnapshotUpdates();
    }

    private void checkForReleaseUpdates(){
        String version = MinecraftVersions.getLatestRelease();
        if(!version.isEmpty() && !version.equals(latestRelease) && MinecraftVersions.hasPatchNote(version)){
            Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(MinecraftVersions.getVersion(version))).block();
            latestRelease = version;
            saveCachedData();
        }
    }

    private void checkForSnapshotUpdates(){
        String version = MinecraftVersions.getLatestSnapshot();
        if(!version.isEmpty() && !version.equals(latestSnapshot) && !version.equals(latestRelease) && MinecraftVersions.hasPatchNote(version)){
            Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(MinecraftVersions.getVersion(version))).block();
            latestSnapshot = version;
            saveCachedData();
        }
    }

    private EmbedData createEmbed(MinecraftVersions.Version version){
        return EmbedData.builder()
                .title(version.title())
                .description(version.description())
                .thumbnail(EmbedThumbnailData.builder().url(version.previewUrl()).build())
                .url(QUILT_PATCH_NOTES.replaceAll("%version%", version.name()))
                .color(GlobalColors.GREEN.getRGB())
                .build();
    }

    private void saveCachedData(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileUtils.createDir("cache");

            FileWriter writer = new FileWriter("cache/minecraft_versions.json");
            writer.write(gson.toJson(new CachedData(latestRelease, latestSnapshot)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CachedData loadCachedData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cache/minecraft_versions.json"));
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            String release = object.get("latestRelease").getAsString();
            String snapshot = object.get("latestSnapshot").getAsString();
            return new CachedData(release, snapshot);
        } catch (FileNotFoundException ignored) {}
        return new CachedData("", "");
    }

    private record CachedData(String latestRelease, String latestSnapshot){}
}
