package ru.pinkgoosik.kitsun.api.modrinth;

import com.google.gson.*;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthUser;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.api.modrinth.entity.SearchResult;
import ru.pinkgoosik.kitsun.feature.KitsunDebug;

import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ModrinthAPI {
    public static final String MOD_URL = "https://modrinth.com/mod/%slug%";
    public static final String API_URL = "https://api.modrinth.com/v2";
    public static final String API_PROJECT_URL = API_URL + "/project/%slug%";
    public static final String API_PROJECT_VERSIONS_URL = API_URL + "/project/%slug%/version";
    public static final String API_USER_URL = API_URL + "/user/%id%";

    private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Optional<ModrinthProject> getProject(String slug) {
        return parseProject(slug);
    }

    private static Optional<ModrinthProject> parseProject(String slug) {
        try {
            URL url = new URL(API_PROJECT_URL.replace("%slug%", slug));
            URLConnection request = url.openConnection();
            request.connect();
            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            ModrinthProject project = GSON.fromJson(reader, ModrinthProject.class);
            return Optional.of(project);
        }
        catch (Exception e) {
            String msg = "Failed to parse modrinth project " + slug + " due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
        return Optional.empty();
    }

    public static Optional<ArrayList<ProjectVersion>> getVersions(String slug) {
        return parseVersions(slug);
    }

    private static Optional<ArrayList<ProjectVersion>> parseVersions(String slug) {
        try {
            URL url = new URL(API_PROJECT_VERSIONS_URL.replace("%slug%", slug));
            URLConnection request = url.openConnection();
            request.connect();
            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            ProjectVersion[] versions = GSON.fromJson(reader, ProjectVersion[].class);
            ArrayList<ProjectVersion> versionsArray = new ArrayList<>(Arrays.asList(versions));
            return Optional.of(versionsArray);
        }
        catch (Exception e) {
            String msg = "Failed to parse modrinth project " + slug + " versions due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
        return Optional.empty();
    }

    public static Optional<ModrinthUser> getUser(String id) {
        return parseUser(id);
    }

    private static Optional<ModrinthUser> parseUser(String id) {
        try {
            URL url = new URL(API_USER_URL.replace("%id%", id));
            URLConnection request = url.openConnection();
            request.connect();
            InputStreamReader reader = new InputStreamReader(request.getInputStream());
            ModrinthUser user = GSON.fromJson(reader, ModrinthUser.class);
            return Optional.of(user);
        }
        catch (Exception e) {
            String msg = "Failed to parse modrinth user " + id + " due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
        return Optional.empty();
    }

    public static Optional<SearchResult> search(SearchRequest request) {
        return parseSearchResult(request);
    }

    private static Optional<SearchResult> parseSearchResult(SearchRequest request) {
        try {
            URL url = new URL(request.getUrl());
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            SearchResult result = GSON.fromJson(reader, SearchResult.class);
            return Optional.of(result);
        }
        catch (Exception e) {
            String msg = "Failed to parse modrinth search result of \"" + request.getUrl() + "\" due to an exception:\n" + e;
            Bot.LOGGER.error(msg);
            KitsunDebug.report(msg, e, false);
        }
        return Optional.empty();
    }

}
