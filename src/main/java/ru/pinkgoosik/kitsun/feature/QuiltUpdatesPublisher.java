package ru.pinkgoosik.kitsun.feature;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import ru.pinkgoosik.kitsun.Bot;
import ru.pinkgoosik.kitsun.api.QuiltMeta;
import ru.pinkgoosik.kitsun.cache.ServerData;
import ru.pinkgoosik.kitsun.util.KitsunColors;

import java.time.Instant;

public class QuiltUpdatesPublisher {
	public boolean enabled = false;
	/**
	 * Discord server ID that this QuiltUpdatesPublisher belongs to
	 */
	public String server;
	/**
	 * Discord server's channel ID for this QuiltUpdatesPublisher publish to
	 */
	public String channel = "";
	/**
	 * Cached Quilt Loader latest version name
	 */
	public String latestVersion = "";

	public QuiltUpdatesPublisher(String serverID) {
		this.server = serverID;
	}

	public void enable(String channelID) {
		this.enabled = true;
		this.channel = channelID;
	}

	public void disable() {
		this.enabled = false;
	}

	public void check() {
		QuiltMeta.getQuiltVersions().ifPresent(versions -> {
			var version = versions.get(0);
			if(!this.latestVersion.equals(version.version)) {
				this.latestVersion = version.version;
				publish(version.version);
			}
		});
	}

	private void publish(String version) {
		ServerData.get(server).save();
		Bot.rest.getChannelById(Snowflake.of(channel)).createMessage(createEmbed(version)).block();
	}

	private EmbedData createEmbed(String version) {
		String linksPart = "\n[Homepage](https://quiltmc.org) | [Source Code](https://github.com/QuiltMC/quilt-loader)";

		return EmbedData.builder().title("New Quilt Loader Version")
				.description("Quilt Loader " + version + " just got released!" + linksPart)
				.thumbnail(EmbedThumbnailData.builder().url("https://github.com/QuiltMC/art/blob/master/brand/512png/quilt_logo_dark.png?raw=true").build())
				.color(KitsunColors.getCyan().getRGB())
				.timestamp(Instant.now().toString())
				.build();
	}

}
