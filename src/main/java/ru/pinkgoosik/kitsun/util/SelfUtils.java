package ru.pinkgoosik.kitsun.util;

import ru.pinkgoosik.kitsun.DiscordApp;

public class SelfUtils {

	public static String getId() {
		return DiscordApp.jda.getSelfUser().getId();
	}
}
