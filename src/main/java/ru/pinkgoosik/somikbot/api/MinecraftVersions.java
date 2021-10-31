package ru.pinkgoosik.somikbot.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class MinecraftVersions {
    private static final String MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
    private static final String LAUNCHER_CONTENT = "https://launchercontent.mojang.com/javaPatchNotes.json";

    public static Version getVersion(String name){
        String title = getTitle(name);
        String description = getDescription(name);
        String previewUrl = getPreviewUrl(name);
        return new Version(name, title, description, previewUrl);
    }

    public static String getLatestRelease(){
        return parseVersion("release");
    }

    public static String getLatestSnapshot(){
        return parseVersion("snapshot");
    }

    private static String parseVersion(String memberName){
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

    public record Version(String name, String title, String description, String previewUrl){}

    private static String getDescription(String versionName){
        String body = getBody(versionName);
        String[] firstParagraph = body.split("<h1>");
        String description = firstParagraph[0];
        description = clearFormation(description);
        description = removeUrls(description);
        return description;
    }

    private static String clearFormation(String string){
        String clean = string;
        clean = clean.replaceAll("<p>", "");
        clean = clean.replaceAll("#x26;", "");
        clean = clean.replaceAll("<a href=\"", "");
        clean = clean.replaceAll("target=\"_blank\" rel=\"noopener noreferrer\">", "");
        clean = clean.replaceAll("</a>", "");
        clean = clean.replaceAll("</p>", " ");
        clean = clean.replaceAll("<strong>", "**");
        clean = clean.replaceAll("</strong>", "**");
        clean = clean.replaceAll("\"", "");
        return clean;
    }

    private static String removeUrls(String str){
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

    private static String getBody(String versionName){
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

    private static String getTitle(String versionName){
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

    private static String getPreviewUrl(String versionName){
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
}
