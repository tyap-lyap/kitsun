package ru.pinkgoosik.somikbot.feature;

import com.google.gson.*;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.RestClient;
import discord4j.rest.util.Color;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class MinecraftUpdates {
    private static final String MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
    private static final String QUILT_PATCH_NOTES = "https://quiltmc.org/mc-patchnotes/#%version%";
    String channel = "900427004069957652";
    long delay = 60;
    String latestRelease;
    String latestSnapshot;
    RestClient client;

    public MinecraftUpdates(RestClient client){
        this.client = client;
        this.latestRelease = loadCachedData().latestRelease();
        this.latestSnapshot = loadCachedData().latestSnapshot();
        this.startScheduler();
    }

    private void startScheduler(){
        new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        checkForReleaseUpdates();
                        checkForSnapshotUpdates();
                    }
                }, delay * 1000, delay * 1000
        );
    }

    private void checkForReleaseUpdates(){
        String version = parse("release");
        if(!version.isEmpty() && !version.equals(latestRelease)){
            client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version)).block();
            latestRelease = version;
            saveCachedData();
        }
    }

    private void checkForSnapshotUpdates(){
        String version = parse("snapshot");
        if(!version.isEmpty() && !version.equals(latestSnapshot)){
            client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version)).block();
            latestSnapshot = version;
            saveCachedData();
        }
    }

    private EmbedData createEmbed(String versionName){
        return EmbedData.builder()
                .title(versionName + " just got released.")
                .url(QUILT_PATCH_NOTES.replaceAll("%version%", versionName))
                .color(Color.of(145,219,105).getRGB())
                .build();
    }

    private String parse(String memberName){
        try {
            URL url = new URL(MANIFEST);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            return jsonElement.getAsJsonObject().get("latest").getAsJsonObject().get(memberName).getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void saveCachedData(){
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            Gson gson = builder.create();
            FileWriter writer = new FileWriter("cached_minecraft_versions.json");
            writer.write(gson.toJson(new CachedData(latestRelease, latestSnapshot)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CachedData loadCachedData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cached_minecraft_versions.json"));
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            String release = object.get("latestRelease").getAsString();
            String snapshot = object.get("latestSnapshot").getAsString();
            return new CachedData(release, snapshot);
        } catch (FileNotFoundException ignored) {}
        return new CachedData("", "");
    }

    private record CachedData(String latestRelease, String latestSnapshot){}
}
