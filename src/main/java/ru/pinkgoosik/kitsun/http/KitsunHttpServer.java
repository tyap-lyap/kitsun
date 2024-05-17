package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpServer;
import ru.pinkgoosik.kitsun.Bot;
import java.net.InetSocketAddress;

public class KitsunHttpServer {
	public static HttpServer server;

	public static void init() {
		if(Bot.secrets.get().http.port > 0) {
			try {
				HttpServer server = HttpServer.create(new InetSocketAddress(Bot.secrets.get().http.port), 0);

				server.createContext("/github", new GithubWebhook());
				server.createContext("/mod_update", new ModUpdateWebhook());

				server.setExecutor(command -> new Thread(command).start());
				server.start();
				KitsunHttpServer.server = server;
			}
			catch (Exception e) {
				Bot.LOGGER.error("Failed to setup http server due to an exception: " + e);
			}
		}
	}

}
