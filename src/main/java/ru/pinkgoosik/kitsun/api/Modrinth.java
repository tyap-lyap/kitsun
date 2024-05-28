package ru.pinkgoosik.kitsun.api;

import masecla.modrinth4j.client.agent.UserAgent;
import masecla.modrinth4j.endpoints.version.GetProjectVersions;
import masecla.modrinth4j.main.ModrinthAPI;
import masecla.modrinth4j.model.project.Project;
import masecla.modrinth4j.model.user.ModrinthUser;
import masecla.modrinth4j.model.version.ProjectVersion;
import ru.pinkgoosik.kitsun.DiscordApp;
import ru.pinkgoosik.kitsun.debug.KitsunDebugWebhook;

import java.util.ArrayList;
import java.util.Optional;

public class Modrinth {
	public static final String PROJECT_URL = "https://modrinth.com/project/%slug%";
	public static final ModrinthAPI API = ModrinthAPI.rateLimited(
			UserAgent.builder().authorUsername("tyap-lyap").projectName("kitsun").projectVersion("latest").build(),
			DiscordApp.secrets.get().modrinthApiKey);

	public static Optional<Project> getProject(String project) {
		try {
			return Optional.of(API.projects().get(project).join());
		}
		catch (Exception e) {
			KitsunDebugWebhook.report("Failed to get modrinth project " + project + " due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static Optional<ArrayList<ProjectVersion>> getVersions(String project) {
		try {
			return Optional.of(new ArrayList<>(API.versions().getProjectVersions(project, GetProjectVersions.GetProjectVersionsRequest.builder().build()).get()));
		}
		catch (Exception e) {
			KitsunDebugWebhook.report("Failed to get modrinth project " + project + " versions due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static Optional<ModrinthUser> getUser(String id) {
		try {
			return Optional.of(API.users().getUser(id).join());
		}
		catch (Exception e) {
			KitsunDebugWebhook.report("Failed to get modrinth user " + id + " due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static String getUrl(Project project) {
		return PROJECT_URL.replace("%slug%", project.getSlug());
	}
}
