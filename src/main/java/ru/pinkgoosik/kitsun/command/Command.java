package ru.pinkgoosik.kitsun.command;

import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import ru.pinkgoosik.kitsun.util.GlobalColors;

public abstract class Command {

    public abstract String getName();

    public abstract String getDescription();

    public String appendName(){
        return "**!" + this.getName() + "**";
    }

    public void respond(CommandUseContext context){}

    public static EmbedData createErrorEmbed(String text){
        return EmbedData.builder()
                .title("Failed")
                .description(text)
                .color(GlobalColors.RED.getRGB())
                .build();
    }

    public static EmbedData createSuccessfulEmbed(String title, String text) {
        return EmbedData.builder()
                .title(title)
                .description(text)
                .color(GlobalColors.GREEN.getRGB())
                .build();
    }

    public static EmbedData createSuccessfulEmbed(String title, String text, String previewUrl){
        return EmbedData.builder()
                .title(title)
                .description(text)
                .color(GlobalColors.GREEN.getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(previewUrl).build())
                .build();
    }

    public static EmbedData createInfoEmbed(String title, String text){
        return EmbedData.builder()
                .title(title)
                .description(text)
                .color(GlobalColors.BLUE.getRGB())
                .build();
    }

}
