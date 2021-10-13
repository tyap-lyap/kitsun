package ru.pinkgoosik.somikbot.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

            ModrinthMod mod = new ModrinthMod();
            mod.iconUrl = object.get("icon_url").getAsString();
            mod.modId = object.get("id").getAsString();
            mod.modSlug = object.get("slug").getAsString();
            mod.title = object.get("title").getAsString();
            mod.shortDescription = object.get("description").getAsString();
            mod.modUrl = MOD_URL_TEMPLATE.replace("%slug%", mod.modSlug);
            mod.versions = tryToGetVersions(mod.modId);
            mod.downloads = object.get("downloads").getAsInt();
            mod.followers = object.get("followers").getAsInt();
            return mod;
        } catch (IOException ignored) {}
        return new ModrinthMod();
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
                ModVersion version = new ModVersion();
                JsonObject object = element.getAsJsonObject();
                version.id = object.get("id").getAsString();
                version.name = object.get("name").getAsString();
                version.version_number = object.get("version_number").getAsString();
                version.changelog = object.get("changelog").getAsString();
                versions.add(version);
            });
            return versions;
        } catch (IOException ignored) {}
        return new ArrayList<>();
    }
}
