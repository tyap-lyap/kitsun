package ru.pinkgoosik.kitsun.api.modrinth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthUser;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ProjectVersion;
import ru.pinkgoosik.kitsun.api.modrinth.entity.SearchResult;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Deprecated
public class ModrinthAPI {
	public static final String PROJECT_URL = "https://modrinth.com/%project_type%/%slug%";
	public static final String API_URL = "https://api.modrinth.com/v2";
	public static final String API_PROJECT_URL = API_URL + "/project/%slug%";
	public static final String API_PROJECT_VERSIONS_URL = API_URL + "/project/%slug%/version";
	public static final String API_USER_URL = API_URL + "/user/%id%";

	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

	public static Optional<ModrinthProject> getProject(String slug) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(API_PROJECT_URL.replace("%slug%", slug)))
					.headers("User-Agent", "tyap-lyap/kitsun/latest")
					.build();

			HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
			InputStreamReader reader = new InputStreamReader(response.body());
			ModrinthProject project = GSON.fromJson(reader, ModrinthProject.class);
			return Optional.of(project);
		}
		catch(FileNotFoundException e) {
			return Optional.empty();
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to parse modrinth project " + slug + " due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static Optional<ArrayList<ProjectVersion>> getVersions(String slug) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(API_PROJECT_VERSIONS_URL.replace("%slug%", slug)))
					.headers("User-Agent", "tyap-lyap/kitsun/latest")
					.build();

			HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
			InputStreamReader reader = new InputStreamReader(response.body());
			ProjectVersion[] versions = GSON.fromJson(reader, ProjectVersion[].class);
			return Optional.of(new ArrayList<>(Arrays.asList(versions)));
		}
		catch(FileNotFoundException e) {
			return Optional.empty();
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to parse modrinth project " + slug + " versions due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static Optional<ModrinthUser> getUser(String id) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(API_USER_URL.replace("%id%", id)))
					.headers("User-Agent", "tyap-lyap/kitsun/latest")
					.build();

			HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
			InputStreamReader reader = new InputStreamReader(response.body());
			ModrinthUser user = GSON.fromJson(reader, ModrinthUser.class);
			return Optional.of(user);
		}
		catch(FileNotFoundException e) {
			return Optional.empty();
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to parse modrinth user " + id + " due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static Optional<SearchResult> search(SearchRequest request) {
		try {
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(request.buildUrl()))
					.headers("User-Agent", "tyap-lyap/kitsun/latest")
					.build();

			HttpResponse<InputStream> response = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofInputStream());
			InputStreamReader reader = new InputStreamReader(response.body());
			SearchResult result = GSON.fromJson(reader, SearchResult.class);
			return Optional.of(result);
		}
		catch(FileNotFoundException e) {
			return Optional.empty();
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to parse modrinth search result of \"" + request.buildUrl() + "\" due to an exception:\n" + e);
		}
		return Optional.empty();
	}

}
