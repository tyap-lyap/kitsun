package ru.pinkgoosik.kitsun.util;

import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;

public class Embeds {

    public static EmbedData error(String text) {
        return EmbedData.builder()
                .title("Failed")
                .description(text)
                .color(GlobalColors.RED.getRGB())
                .build();
    }

    public static EmbedData success(String title, String text) {
        return EmbedData.builder()
                .title(title)
                .description(text)
                .color(GlobalColors.GREEN.getRGB())
                .build();
    }

    public static EmbedData success(String title, String text, String previewUrl) {
        return EmbedData.builder()
                .title(title)
                .description(text)
                .color(GlobalColors.GREEN.getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(previewUrl).build())
                .build();
    }

    public static EmbedData info(String title, String text) {
        return EmbedData.builder()
                .title(title)
                .description(text)
                .color(GlobalColors.BLUE.getRGB())
                .build();
    }
}
