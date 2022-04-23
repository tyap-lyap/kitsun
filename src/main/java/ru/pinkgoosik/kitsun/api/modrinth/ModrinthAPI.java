package ru.pinkgoosik.kitsun.api.modrinth;

import com.google.gson.*;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthUser;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.feature.KitsunDebug;

import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ModrinthAPI {
    public static final String MOD_URL = "https://modrinth.com/mod/%slug%";
    public static final String API_PROJECT_URL = "https://api.modrinth.com/v2/project/%slug%";
    public static final String API_PROJECT_VERSIONS_URL = "https://api.modrinth.com/v2/project/%slug%/version";
    public static final String API_USER_URL = "https://api.modrinth.com/v2/user/%id%";

    public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();

    public static Optional<ModrinthProject> getProject(String slug) {
        return parseProject(slug);
    }

    public static Optional<ArrayList<ProjectVersion>> getVersions(String slug) {
        return parseVersions(slug);
    }

    public static Optional<ModrinthUser> getUser(String id) {
        return parseUser(id);
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

}
