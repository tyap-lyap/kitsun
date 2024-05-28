package ru.pinkgoosik.kitsun.api.curseforge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.pinkgoosik.kitsun.DiscordApp;
import ru.pinkgoosik.kitsun.api.curseforge.entity.CurseForgeMod;
import ru.pinkgoosik.kitsun.debug.KitsunDebugWebhook;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class CurseForgeAPI {
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
	private static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	public static final String API_URL = "https://api.curseforge.com/v1";
	public static final String API_MOD_URL = API_URL + "/mods/%id%";

	public static Optional<CurseForgeMod> getMod(String id) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.GET()
					.uri(new URI(API_MOD_URL.replace("%id%", id)))
					.headers("x-api-key", DiscordApp.secrets.get().curseforgeApiKey)
					.build();

			HttpResponse<InputStream> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
			InputStreamReader reader = new InputStreamReader(response.body());
			CurseForgeMod mod = GSON.fromJson(reader, CurseForgeMod.class);
			return Optional.of(mod);
		}
		catch(FileNotFoundException e) {
			return Optional.empty();
		}
		catch(Exception e) {
			KitsunDebugWebhook.report("Failed to parse curseforge mod " + id + " due to an exception:\n" + e);
		}
		return Optional.empty();
	}
}
