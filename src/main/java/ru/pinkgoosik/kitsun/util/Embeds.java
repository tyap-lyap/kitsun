package ru.pinkgoosik.kitsun.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class Embeds {

	public static MessageEmbed error(String text) {
		var embed = new EmbedBuilder();
		embed.setTitle("Failed");
		embed.setDescription(text);
		embed.setColor(KitsunColors.getRed());
		return embed.build();
	}

	public static MessageEmbed success(String title, String text) {
		var embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(text);
		embed.setColor(KitsunColors.getGreen());
		return embed.build();
	}

	public static MessageEmbed success(String title, String text, String previewUrl) {
		var embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(text);
		embed.setColor(KitsunColors.getGreen());
		embed.setThumbnail(previewUrl);
		return embed.build();
	}

	public static MessageEmbed info(String title, String text) {
		var embed = new EmbedBuilder();
		embed.setTitle(title);
		embed.setDescription(text);
		embed.setColor(KitsunColors.getBlue());
		return embed.build();
	}
}
