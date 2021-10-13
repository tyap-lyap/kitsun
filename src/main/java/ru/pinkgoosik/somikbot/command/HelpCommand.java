package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.util.Color;

public class HelpCommand extends Command {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "Sends list of available commands.";
    }

    @Override
    public void respond(MessageCreateEvent event, User user, MessageChannel channel) {
        StringBuilder string = new StringBuilder();
        for (Command command : Commands.COMMANDS){
            string.append(command.appendName()).append("\n");
            string.append(command.getDescription()).append("\n \n");
        }
        event.getClient().getRestClient().getChannelById(channel.getId()).createMessage(createEmbed(string.toString(), user)).block();
    }

    private EmbedData createEmbed(String commands, User user){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!help`")
                .description(commands)
                .color(Color.of(96,141,238).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(user.getAvatarUrl()).build())
                .build();
    }
}
