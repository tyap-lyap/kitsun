package ru.pinkgoosik.kitsun.util;

import ru.pinkgoosik.kitsun.Bot;

public class SelfUtils {

	public static String getId() {
		return Bot.jda.getSelfUser().getId();
	}
}
