package ru.pinkgoosik.kitsun.util;

import discord4j.core.spec.EmbedCreateSpec;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;

public class Embeds {

    public static EmbedData error(String text) {
        var embed = EmbedData.builder();
        embed.title("Failed");
        embed.description(text);
        embed.color(KitsunColors.getRed().getRGB());
        return embed.build();
    }

    public static EmbedCreateSpec errorSpec(String text) {
        var embed = EmbedCreateSpec.builder();
        embed.title("Failed");
        embed.description(text);
        embed.color(KitsunColors.getRed());
        return embed.build();
    }

    public static EmbedData success(String title, String text) {
        var embed = EmbedData.builder();
        embed.title(title);
        embed.description(text);
        embed.color(KitsunColors.getGreen().getRGB());
        return embed.build();
    }

    public static EmbedData success(String title, String text, String previewUrl) {
        var embed = EmbedData.builder();
        embed.title(title);
        embed.description(text);
        embed.color(KitsunColors.getGreen().getRGB());
        embed.thumbnail(EmbedThumbnailData.builder().url(previewUrl).build());
        return embed.build();
    }

    public static EmbedData info(String title, String text) {
        var embed = EmbedData.builder();
        embed.title(title);
        embed.description(text);
        embed.color(KitsunColors.getBlue().getRGB());
        return embed.build();
    }
}
