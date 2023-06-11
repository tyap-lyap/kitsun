package ru.pinkgoosik.kitsun.feature;

public class AutoReaction {
	public String server;
	public String regex;
	public String emoji;
	public boolean unicode = false;

	public boolean shouldBeRemoved = false;

	public AutoReaction(String serverId, String regex, String emoji) {
		this.server = serverId;
		this.regex = regex;
		this.emoji = emoji;
	}

	public AutoReaction(String serverId, String regex, String emoji, boolean unicode) {
		this.server = serverId;
		this.regex = regex;
		this.emoji = emoji;
		this.unicode = unicode;
	}
}
