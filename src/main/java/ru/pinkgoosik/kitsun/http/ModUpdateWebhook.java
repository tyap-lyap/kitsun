package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpExchange;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class ModUpdateWebhook extends KitsunHttpHandler {

	@Override
	public void handle(HttpExchange exchange) {
		var map = parseParams(exchange);

		if (map.containsKey("token") && Bot.secrets.get().http.token.equals(map.get("token"))) {
			if(map.containsKey("server") && map.containsKey("project")) {
				if (!ServerUtils.exist(map.get("server"))) {
					success(exchange, "Server not found");
					return;
				}
				var data = ServerData.get(map.get("server"));

				for(var publisher : data.modUpdates.get()) {
					if(publisher.project.equals(map.get("project"))) {
						success(exchange, "Success");
						try {
							Thread.sleep(5 * 1000);
							publisher.check(0);
						}
						catch (Exception ignored) {}
						return;
					}
				}
				success(exchange, "Project not found");
				return;
			}
		}

		notFound(exchange);
	}
}
