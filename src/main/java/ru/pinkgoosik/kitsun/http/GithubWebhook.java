package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpExchange;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.feature.KitsunDebugger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class GithubWebhook extends KitsunHttpHandler {

	@Override
	public void handle(HttpExchange exchange) {
		var map = parseParams(exchange);

		if(map.containsKey("token") && Bot.secrets.get().http.token.equals(map.get("token")) && exchange.getRequestMethod().equals("POST") && exchange.getRequestHeaders().containsKey("x-github-event")) {
			var event = exchange.getRequestHeaders().get("x-github-event").get(0);
			var is = exchange.getRequestBody();
			StringBuilder textBuilder = new StringBuilder();

			try (Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
				int c;
				while ((c = reader.read()) != -1) {
					textBuilder.append((char) c);
				}
			}
			catch (Exception e) {
				Bot.LOGGER.info("Failed to handle github webhook due to an exception: " + e);
			}
			String body = textBuilder.toString();

			KitsunDebugger.info(event + ": \n" + body);

			success(exchange, "Accepted", 202);
			return;
		}

		notFound(exchange);
	}
}
