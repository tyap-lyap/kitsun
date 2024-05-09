package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class KitsunHttpServer {
	public static HttpServer server;

	public static void init() {
		if(Bot.secrets.get().httpPort > 0) {
			try {
				HttpServer server = HttpServer.create(new InetSocketAddress(Bot.secrets.get().httpPort), 0);
				server.createContext("/mod_update", exchange -> {
					var map = parseParams(exchange);

					if (map.containsKey("token") && Bot.secrets.get().httpToken.equals(map.get("token"))) {
						success(exchange, "Success");
						if(map.containsKey("server") && map.containsKey("project")) modUpdateWebhook(map.get("server"), map.get("project"));
					}

					notFound(exchange);
				});

				server.setExecutor(command -> new Thread(command).start());
				server.start();
				KitsunHttpServer.server = server;
			}
			catch (Exception e) {
				Bot.LOGGER.error("Failed to setup http server due to an exception: " + e);
			}
		}
	}

	static void modUpdateWebhook(String server, String project) {
		var data = ServerData.get(server);

		for(var publisher : data.modUpdates.get()) {
			if(publisher.project.equals(project)) {
				try {
					Thread.sleep(5 * 1000);
					publisher.check(0);
				}
				catch (Exception ignored) {}
				return;
			}
		}
	}

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
}
