package ru.pinkgoosik.kitsun.api.mojang;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;
import ru.pinkgoosik.kitsun.util.UrlParser;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Optional;

public class MojangAPI {
	private static final String URL_STRING = "https://api.mojang.com/users/profiles/minecraft/%nickname%";
	private static final String MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

	private static ArrayList<String> mcVersionsCache = new ArrayList<>();

	public static Optional<VersionManifest> getManifest() {
		try {
			return Optional.of(UrlParser.get(MANIFEST, VersionManifest.class));
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to parse minecraft versions manifest due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	public static ArrayList<String> getMcVersionsCache() {
		if(mcVersionsCache.isEmpty()) {
			updateMcVersionsCache();
		}
		return mcVersionsCache;
	}

	public static void updateMcVersionsCache() {
		MojangAPI.getManifest().ifPresent(versionManifest -> {
			ArrayList<String> versions = new ArrayList<>();
			versionManifest.versions.forEach(version -> versions.add(version.id));
			mcVersionsCache = versions;
		});
	}

	public static void setMcVersionsCache(VersionManifest manifest) {
		ArrayList<String> versions = new ArrayList<>();
		manifest.versions.forEach(version -> versions.add(version.id));
		mcVersionsCache = versions;
	}

	public static Optional<String> getUuid(String nickname) {
		try {
			URL url = new URL(URL_STRING.replace("%nickname%", nickname));
			URLConnection request = url.openConnection();
			request.connect();
			JsonElement element = JsonParser.parseReader(new InputStreamReader(request.getInputStream()));
			return Optional.of(fromTrimmed(element.getAsJsonObject().get("id").getAsString()));
		}
		catch(Exception e) {
			KitsunDebugger.report("Failed to parse " + nickname + "'s UUID due to an exception:\n" + e);
			return Optional.empty();
		}
	}

	private static String fromTrimmed(String trimmedUUID) {
		try {
			StringBuilder builder = new StringBuilder(trimmedUUID.trim());
			builder.insert(20, "-");
			builder.insert(16, "-");
			builder.insert(12, "-");
			builder.insert(8, "-");
			return builder.toString();
		}
		catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
