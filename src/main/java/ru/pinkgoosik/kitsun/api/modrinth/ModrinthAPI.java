package ru.pinkgoosik.kitsun.api.modrinth;

import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthUser;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.api.modrinth.entity.SearchResult;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.UrlParser;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class ModrinthAPI {
    public static final String PROJECT_URL = "https://modrinth.com/%project_type%/%slug%";
    public static final String API_URL = "https://api.modrinth.com/v2";
    public static final String API_PROJECT_URL = API_URL + "/project/%slug%";
    public static final String API_PROJECT_VERSIONS_URL = API_URL + "/project/%slug%/version";
    public static final String API_USER_URL = API_URL + "/user/%id%";

    public static Optional<ModrinthProject> getProject(String slug) {
        try {
            return Optional.of(UrlParser.get(API_PROJECT_URL.replace("%slug%", slug), ModrinthProject.class));
        }
        catch (FileNotFoundException e) {
            return Optional.empty();
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse modrinth project " + slug + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    public static Optional<ArrayList<ProjectVersion>> getVersions(String slug) {
        try {
            ProjectVersion[] versions = UrlParser.get(API_PROJECT_VERSIONS_URL.replace("%slug%", slug), ProjectVersion[].class);
            return Optional.of(new ArrayList<>(Arrays.asList(versions)));
        }
        catch (FileNotFoundException e) {
            return Optional.empty();
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse modrinth project " + slug + " versions due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    public static Optional<ModrinthUser> getUser(String id) {
        try {
            return Optional.of(UrlParser.get(API_USER_URL.replace("%id%", id), ModrinthUser.class));
        }
        catch (FileNotFoundException e) {
            return Optional.empty();
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse modrinth user " + id + " due to an exception:\n" + e);
        }
        return Optional.empty();
    }

    public static Optional<SearchResult> search(SearchRequest request) {
        try {
            return Optional.of(UrlParser.get(request.getUrl(), SearchResult.class));
        }
        catch (FileNotFoundException e) {
            return Optional.empty();
        }
        catch (Exception e) {
            KitsunDebugger.report("Failed to parse modrinth search result of \"" + request.getUrl() + "\" due to an exception:\n" + e);
        }
        return Optional.empty();
    }

}
