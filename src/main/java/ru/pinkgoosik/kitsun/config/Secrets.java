package ru.pinkgoosik.kitsun.config;

public class Secrets {
	public String curseforgeApiKey = "";
	public String modrinthApiKey = "";
	public String discordToken = "";
	public String githubToken = "";
	public String note = "";
	public String activity = "";
	public HttpConfig http = new HttpConfig();

	public static final Secrets DEFAULT = new Secrets();

	public static class HttpConfig {
		public String hostname = "";
		public int port = 0;
		public String token = "";
	}
}
