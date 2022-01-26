package ru.pinkgoosik.kitsun.api.modrinth;

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
import java.util.Optional;

public class ModrinthAPI {
    public static final String MODRINTH_MOD_URL = "https://modrinth.com/mod/%slug%";
    public static final String MODRINTH_API_MOD_URL = "https://api.modrinth.com/api/v1/mod/%slug%";
    public static final String MODRINTH_MOD_VERSIONS_URL = "https://api.modrinth.com/api/v1/mod/%mod_id%/version";

    public static Optional<ModrinthMod> getMod(String slug) {
        return tryToParse(slug);
    }

    private static Optional<ModrinthMod> tryToParse(String slug) {
        try {
            URL url = new URL(MODRINTH_API_MOD_URL.replace("%slug%", slug));
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
            modUrl = MODRINTH_MOD_URL.replace("%slug%", modSlug);
            versions = tryToGetVersions(modId).orElseGet(ArrayList::new);
            downloads = object.get("downloads").getAsInt();
            followers = object.get("followers").getAsInt();

            return Optional.of(new ModrinthMod(modUrl, iconUrl, modId, modSlug, title, shortDescription, downloads, followers, versions));
        } catch (IOException ignored) {}
        return Optional.empty();
    }

    private static Optional<ArrayList<ModVersion>> tryToGetVersions(String modId) {
        try {
            URL url = new URL(MODRINTH_MOD_VERSIONS_URL.replace("%mod_id%", modId));
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
            return Optional.of(versions);
        } catch (IOException ignored) {}
        return Optional.empty();
    }
}
