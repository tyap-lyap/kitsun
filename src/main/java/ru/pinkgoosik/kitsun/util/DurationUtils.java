package ru.pinkgoosik.kitsun.util;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.format.DateTimeParseException;

public class DurationUtils {

	@Nullable
	public static Duration parse(String str) {
		try {
            return Duration.parse("pt" + str);
		}
		catch (DateTimeParseException e) {
			System.out.println("String cant be parsed");
		}
		return null;
	}

	public static String format(Duration duration) {
		long seconds = duration.getSeconds();
		long absSeconds = Math.abs(seconds);
		String positive = String.format(
			"%dh%02dm%02ds",
			absSeconds / 3600,
			(absSeconds % 3600) / 60,
			absSeconds % 60);
		return seconds < 0 ? "-" + positive : positive;
	}
}
