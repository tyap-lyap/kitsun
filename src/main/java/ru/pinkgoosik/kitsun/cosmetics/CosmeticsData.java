package ru.pinkgoosik.kitsun.cosmetics;

import ru.pinkgoosik.kitsun.util.UrlParser;

import java.util.*;

public class CosmeticsData {
	private static final String URL_STRING = "https://pinkgoosik-assets.akamaized.net/cosmetics/data.json?_=%random%";
	public static final List<EntryData> ENTRIES = new ArrayList<>();

	public static void register(String discord, String name, String uuid) {
		EntryData entry = new EntryData();
		EntryData.UserData user = new EntryData.UserData();
		user.discord = discord;
		user.name = name;
		user.uuid = uuid;
		entry.user = user;

		EntryData.CapeData cape = new EntryData.CapeData();
		cape.name = "";
		cape.glint = false;
		entry.cape = cape;

		ENTRIES.add(entry);
	}

	public static void unregister(String discord) {
		ENTRIES.removeIf(entry -> entry.user.discord.equals(discord));
	}

	public static Optional<EntryData> getEntry(String discord) {
		for(var entry : CosmeticsData.ENTRIES) {
			if(entry.user.discord.equals(discord)) return Optional.of(entry);
		}
		return Optional.empty();
	}

	public static Optional<EntryData> getEntryByName(String name) {
		for(var entry : CosmeticsData.ENTRIES) {
			if(entry.user.name.equals(name)) return Optional.of(entry);
		}
		return Optional.empty();
	}

	public static void setCape(String username, String capeName) {
		for(var entry : ENTRIES) {
			if(entry.user.name.equals(username)) {
				entry.cape.name = capeName;
			}
		}
	}

	public static void clearCape(String username) {
		for(var entry : ENTRIES) {
			if(entry.user.name.equals(username)) {
				EntryData.CapeData cape = new EntryData.CapeData();
				cape.name = "";
				cape.glint = false;
				entry.cape = cape;
			}
		}
	}

	public static boolean hasCape(String nickname) {
		for(var entry : ENTRIES) {
			if(entry.user.name.equals(nickname)) {
				return !entry.cape.name.isBlank();
			}
		}
		return false;
	}

	public static void fetch() {
		try {
			String urlStr = URL_STRING;
			urlStr = urlStr.replaceAll("%random%", random());
			EntryData[] entries = UrlParser.get(urlStr, EntryData[].class);
			ENTRIES.addAll(Arrays.asList(entries));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	static String random() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("_", "");
	}
}
