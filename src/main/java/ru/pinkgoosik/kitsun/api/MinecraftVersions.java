package ru.pinkgoosik.kitsun.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class MinecraftVersions {
    private static final String MANIFEST = "https://launchermeta.mojang.com/mc/game/version_manifest_v2.json";
    private static final String LAUNCHER_CONTENT = "https://launchercontent.mojang.com/javaPatchNotes.json";
    
    public static boolean hasPatchNote(String name) {
        return getTitle(name).isPresent();
    }

    public static Optional<Version> getVersion(String name) {
        var title = getTitle(name);
        var description = getDescription(name);
        var previewUrl = getPreviewUrl(name);
        if(title.isPresent() && description.isPresent() && previewUrl.isPresent()) {
            return Optional.of(new Version(name, title.get(), description.get(), previewUrl.get()));
        }
        return Optional.empty();
    }

    public static Optional<String> getLatestRelease() {
        return parseVersion("release");
    }

    public static Optional<String> getLatestSnapshot() {
        return parseVersion("snapshot");
    }

    private static Optional<String> parseVersion(String memberName) {
        try {
            URL url = new URL(MANIFEST);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            return Optional.of(jsonElement.getAsJsonObject().get("latest").getAsJsonObject().get(memberName).getAsString());
        } catch (IOException ignored) {}
        return Optional.empty();
    }

    public record Version(String name, String title, String description, String previewUrl) {}

    private static Optional<String> getDescription(String versionName) {
        var body = getBody(versionName);
        if(body.isPresent()) {
            String[] firstParagraph = body.get().split("<h1>");
            String description = firstParagraph[0];
            description = clearFormation(description);
            return Optional.of(removeUrls(description));
        }
        return Optional.empty();
    }

    private static String clearFormation(String string) {
        String clean = string;
        clean = clean.replace("<p>", "");
        clean = clean.replace("#x26;", "");
        clean = clean.replace("<a href=\"", "");
        clean = clean.replace("target=\"_blank\" rel=\"noopener noreferrer\">", "");
        clean = clean.replace("</a>", "");
        clean = clean.replace("</p>", " ");
        clean = clean.replace("<strong>", "**");
        clean = clean.replace("</strong>", "**");
        clean = clean.replace("\"", "");
        return clean;
    }

    private static String removeUrls(String str) {
        String[] words = str.split(" ");
        StringBuilder newString = new StringBuilder();
        for(String word : words) {
            if(!word.contains("https://")) {
                newString.append(word);
                newString.append(" ");
            }
        }
        return newString.toString();
    }

    private static Optional<String> getBody(String versionName) {
        try {
            URL url = new URL(LAUNCHER_CONTENT);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray entries = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
            for (JsonElement element : entries) {
                if(element.getAsJsonObject().get("version").getAsString().equals(versionName)) {
                    return Optional.of(element.getAsJsonObject().get("body").getAsString());
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private static Optional<String> getTitle(String versionName) {
        try {
            URL url = new URL(LAUNCHER_CONTENT);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray entries = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
            for (JsonElement element : entries) {
                if(element.getAsJsonObject().get("version").getAsString().equals(versionName)) {
                    return Optional.of(element.getAsJsonObject().get("title").getAsString());
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }

    private static Optional<String> getPreviewUrl(String versionName) {
        try {
            URL url = new URL(LAUNCHER_CONTENT);
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray entries = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
            for (JsonElement element : entries) {
                if(element.getAsJsonObject().get("version").getAsString().equals(versionName)) {
                    String firstPart = "https://launchercontent.mojang.com";
                    String secondPart = element.getAsJsonObject().get("image").getAsJsonObject().get("url").getAsString();
                    return Optional.of(firstPart + secondPart);
                }
            }
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}
