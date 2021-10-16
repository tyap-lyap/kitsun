package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.util.Color;

public abstract class Command {

    public abstract String getName();

    public abstract String getDescription();

    public String appendName(){
        return "**!" + this.getName() + "**";
    }

    public void respond(MessageCreateEvent event, String[] args){}

    public static EmbedData createErrorEmbed(String text){
        return EmbedData.builder()
                .title("Error")
                .description(text)
                .color(Color.of(246,129,129).getRGB())
                .build();
    }
}
