package ru.pinkgoosik.kitsun.api.mojang;

import com.google.gson.*;
import ru.pinkgoosik.kitsun.Bot;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

public class PatchNotes {
	public static final Gson GSON = new GsonBuilder().setLenient().setPrettyPrinting().create();
	private static final String LAUNCHER_CONTENT = "https://launchercontent.mojang.com/v2/javaPatchNotes.json";

	public static Optional<PatchNotesEntry> getEntry(String version) {
		try {
			URL url = new URL(LAUNCHER_CONTENT);
			URLConnection request = url.openConnection();
			request.connect();
			JsonElement jsonElement = JsonParser.parseReader(new InputStreamReader(request.getInputStream()));
			JsonArray jsonArray = jsonElement.getAsJsonObject().get("entries").getAsJsonArray();
			PatchNotesEntry[] entries = GSON.fromJson(jsonArray, PatchNotesEntry[].class);
			for(var entry : entries) {
				if(entry.version.equals(version)) return Optional.of(entry);
			}
		}
		catch(Exception e) {
			Bot.LOGGER.error("Failed to parse patch note entry due to an exception:\n" + e);
		}
		return Optional.empty();
	}

	@SuppressWarnings("unused")
	public static class PatchNotesEntry {
		public String title = "";
		public String version = "";
		public String type = "";
		public Image image = new Image();
		public String contentPath = "";
		public String id = "";
		public String date = "";
		public String shortText = "";

		public String summary() {
			return shortText;
//			String[] firstParagraph = body.split("<h1>");
//			String description = firstParagraph[0];
//			description = clearFormation(description);
//			return removeUrls(description);
		}

		private String clearFormation(String content) {
			String str = content;
			str = str.replace("<p>", "").replace("#x26;", "");
			str = str.replace("<a href=\"", "").replace("</a>", "");
			str = str.replace("target=\"_blank\" rel=\"noopener noreferrer\">", "");
			str = str.replace("</p>", " ").replace("<strong>", "**");
			str = str.replace("</strong>", "**").replace("\"", "");
			str = str.replace("<em>", "").replace("</em>", "");
			str = str.replace("<hr>", "");
			return str;
		}

		private String removeUrls(String str) {
			String[] words = str.split(" ");
			StringBuilder newString = new StringBuilder();
			for(String word : words) {
				if(!word.contains("https://")) {
					newString.append(word);
					newString.append(" ");
				}
			}
			return newString.toString();
		}
	}

	@SuppressWarnings("unused")
	public static class Image {
		public String url = "";
		public String title = "";

		public String getFullUrl() {
			return "https://launchercontent.mojang.com" + url;
		}
	}

}
