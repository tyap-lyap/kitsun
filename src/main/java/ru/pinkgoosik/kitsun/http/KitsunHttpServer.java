package ru.pinkgoosik.kitsun.http;

import com.sun.net.httpserver.HttpServer;
import ru.pinkgoosik.kitsun.DiscordApp;
import java.net.InetSocketAddress;

public class KitsunHttpServer {
	public static HttpServer server;

	public static void init() {
		if(DiscordApp.secrets.get().http.port > 0) {
			try {
				HttpServer server = HttpServer.create(new InetSocketAddress(DiscordApp.secrets.get().http.hostname, DiscordApp.secrets.get().http.port), 0);

				server.createContext("/github", new GithubWebhook());
				server.createContext("/mod_update", new ModUpdateWebhook());

				server.setExecutor(command -> new Thread(command).start());
				server.start();
				KitsunHttpServer.server = server;
			}
			catch (Exception e) {
				DiscordApp.LOGGER.error("Failed to setup http server due to an exception: " + e);
			}
		}
	}

}
