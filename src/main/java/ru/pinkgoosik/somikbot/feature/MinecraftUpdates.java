package ru.pinkgoosik.somikbot.feature;

import com.google.gson.*;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import ru.pinkgoosik.somikbot.Bot;
import ru.pinkgoosik.somikbot.util.FileUtils;
import ru.pinkgoosik.somikbot.util.GlobalColors;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class MinecraftUpdates {
    private static final String MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
    private static final String QUILT_PATCH_NOTES = "https://quiltmc.org/mc-patchnotes/#%version%";
    private static final String LAUNCHER_CONTENT = "https://launchercontent.mojang.com/javaPatchNotes.json";
    String channel = "900427004069957652";
    long delay = 300;
    String latestRelease;
    String latestSnapshot;

    public MinecraftUpdates(){
        CachedData data = loadCachedData();
        this.latestRelease = data.latestRelease();
        this.latestSnapshot = data.latestSnapshot();
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
            Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version)).block();
            latestRelease = version;
            saveCachedData();
        }
    }

    private void checkForSnapshotUpdates(){
        String version = parse("snapshot");
        if(!version.isEmpty() && !version.equals(latestSnapshot)){
            Bot.client.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version)).block();
            latestSnapshot = version;
            saveCachedData();
        }
    }

    private EmbedData createEmbed(String versionName){
        return EmbedData.builder()
                .title(getTitle(versionName))
                .description(getShortDescription(versionName))
                .thumbnail(EmbedThumbnailData.builder().url(getImageUrl(versionName)).build())
                .url(QUILT_PATCH_NOTES.replaceAll("%version%", versionName))
                .color(GlobalColors.GREEN.getRGB())
                .build();
    }

    private String getShortDescription(String versionName){
        String body = getBody(versionName);
        String[] firstParagraph = body.split("<h1>");
        return clear(firstParagraph[0]);
    }

    private String clear(String string){
        String desc = string;
        desc = desc.replaceAll("<p>", "");
        desc = desc.replaceAll("#x26;", "");
        desc = desc.replaceAll("<a href=\"", "");
        desc = desc.replaceAll("target=\"_blank\" rel=\"noopener noreferrer\">", "");
        desc = desc.replaceAll("</a>", "");
        desc = desc.replaceAll("</p>", " ");
        desc = desc.replaceAll("<strong>", "**");
        desc = desc.replaceAll("</strong>", "**");
        desc = desc.replaceAll("\"", "");
        return removeUrls(desc);
    }

    private String removeUrls(String str){
        String[] words = str.split(" ");
        StringBuilder newString = new StringBuilder();
        for(String word : words){
            if(!word.contains("https://")){
                newString.append(word);
                newString.append(" ");
            }
        }
        return newString.toString();
    }

    private String getBody(String versionName){
        try {
            URL url = new URL(LAUNCHER_CONTENT);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray entries = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
            for (JsonElement element : entries){
                if(element.getAsJsonObject().get("version").getAsString().equals(versionName)){
                    return element.getAsJsonObject().get("body").getAsString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getTitle(String versionName){
        try {
            URL url = new URL(LAUNCHER_CONTENT);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray entries = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
            for (JsonElement element : entries){
                if(element.getAsJsonObject().get("version").getAsString().equals(versionName)){
                    return element.getAsJsonObject().get("title").getAsString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getImageUrl(String versionName){
        try {
            URL url = new URL(LAUNCHER_CONTENT);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray entries = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
            for (JsonElement element : entries){
                if(element.getAsJsonObject().get("version").getAsString().equals(versionName)){
                    String firstPart = "https://launchercontent.mojang.com";
                    String secondPart = element.getAsJsonObject().get("image").getAsJsonObject().get("url").getAsString();
                    return firstPart + secondPart;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
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
            FileUtils.createDir("cache");

            FileWriter writer = new FileWriter("cache/cached_minecraft_versions.json");
            writer.write(gson.toJson(new CachedData(latestRelease, latestSnapshot)));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private CachedData loadCachedData(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader("cache/cached_minecraft_versions.json"));
            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
            String release = object.get("latestRelease").getAsString();
            String snapshot = object.get("latestSnapshot").getAsString();
            return new CachedData(release, snapshot);
        } catch (FileNotFoundException ignored) {}
        return new CachedData("", "");
    }

    private record CachedData(String latestRelease, String latestSnapshot){}
}
