package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.pinkgoosik.kitsun.Bot;

import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class KitsunHttpHandler implements HttpHandler {

	static Map<String, String> parseParams(HttpExchange exchange) {
		String[] split = exchange.getRequestURI().toString().split("\\?");
		if (split.length >= 2) {
			return splitParams(split[1]);
		}
		return new LinkedHashMap<>();
	}

	static Map<String, String> splitParams(String params) {
		Map<String, String> queryPairs = new LinkedHashMap<>();

		try {
			String[] pairs = params.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf("=");
				queryPairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
			}
		}
		catch (Exception e) {
			Bot.LOGGER.error("Failed to split parameters: " + params + ", " + e);
		}
		return queryPairs;
	}

	static void notFound(HttpExchange exchange) {
		try {
			String text = "<h1>404 Not Found</h1>No context found for request";
			exchange.sendResponseHeaders(404, text.length());
			OutputStream os = exchange.getResponseBody();

			os.write(text.getBytes());
			os.flush();
			exchange.close();
		}
		catch (Exception e) {
			Bot.LOGGER.error("Failed to send not found response " + e);
		}
	}

	static void success(HttpExchange exchange, String text) {
		try {
			exchange.sendResponseHeaders(200, text.length());
			OutputStream os = exchange.getResponseBody();
			os.write(text.getBytes());
			os.close();
		}
		catch (Exception e) {
			Bot.LOGGER.error("Failed to send success response " + e);
		}
	}

	static void success(HttpExchange exchange, String text, int status) {
		try {
			exchange.sendResponseHeaders(status, text.length());
			OutputStream os = exchange.getResponseBody();
			os.write(text.getBytes());
			os.close();
		}
		catch (Exception e) {
			Bot.LOGGER.error("Failed to send success response " + e);
		}
	}
}
