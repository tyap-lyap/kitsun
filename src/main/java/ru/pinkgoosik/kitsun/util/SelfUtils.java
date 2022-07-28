package ru.pinkgoosik.kitsun.util;

import reactor.core.publisher.Mono;
import ru.pinkgoosik.kitsun.Bot;

public class SelfUtils {

	public static String getId() {
		return (String) Bot.rest.getSelf().flatMap(userData -> Mono.create((sink) -> sink.success(userData.id().asString()))).block();
	}
}
