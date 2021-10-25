package ru.pinkgoosik.somikbot.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class ModrinthAPI {
    public static final String MOD_URL_TEMPLATE = "https://modrinth.com/mod/%slug%";
    public static final String API_MOD_URL_TEMPLATE = "https://api.modrinth.com/api/v1/mod/%slug%";
    public static final String MOD_VERSIONS_URL_TEMPLATE = "https://api.modrinth.com/api/v1/mod/%mod_id%/version";

    @Nullable
    public static ModrinthMod getModBySlug(String slug){
        return tryToParse(slug);
    }

    private static ModrinthMod tryToParse(String slug){
        try {
            URL url = new URL(API_MOD_URL_TEMPLATE.replace("%slug%", slug));
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonObject object = jsonElement.getAsJsonObject();

            String modUrl, iconUrl, modId, modSlug, title, shortDescription;
            int downloads, followers;
            ArrayList<ModVersion> versions;

            iconUrl = object.get("icon_url").getAsString();
            modId = object.get("id").getAsString();
            modSlug = object.get("slug").getAsString();
            title = object.get("title").getAsString();
            shortDescription = object.get("description").getAsString();
            modUrl = MOD_URL_TEMPLATE.replace("%slug%", modSlug);
            versions = tryToGetVersions(modId);
            downloads = object.get("downloads").getAsInt();
            followers = object.get("followers").getAsInt();

            return new ModrinthMod(modUrl, iconUrl, modId, modSlug, title, shortDescription, downloads, followers, versions);
        } catch (IOException ignored) {}
        return null;
    }

    private static ArrayList<ModVersion> tryToGetVersions(String modId){
        try {
            URL url = new URL(MOD_VERSIONS_URL_TEMPLATE.replace("%mod_id%", modId));
            URLConnection request = url.openConnection();
            request.connect();
            JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader((InputStream)request.getContent()));
            JsonArray array = jsonElement.getAsJsonArray();

            ArrayList<ModVersion> versions = new ArrayList<>();

            array.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                String id, name, changelog;
                id = object.get("id").getAsString();
                name = object.get("name").getAsString();
                changelog = object.get("changelog").getAsString();
                versions.add(new ModVersion(id, name, changelog));
            });
            return versions;
        } catch (IOException ignored) {}
        return new ArrayList<>();
    }
}
