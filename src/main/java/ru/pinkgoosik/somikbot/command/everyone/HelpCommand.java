package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.Commands;
import ru.pinkgoosik.somikbot.util.GlobalColors;

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
    public void respond(MessageCreateEvent event, String[] args) {
        User user;
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        RestChannel restChannel;

        if(event.getMessage().getAuthor().isEmpty()) return;
        else user = event.getMessage().getAuthor().get();
        if(channel == null) return;
        else restChannel = event.getClient().getRestClient().getChannelById(channel.getId());

        StringBuilder text = new StringBuilder();
        for (Command command : Commands.COMMANDS){
            text.append(command.appendName()).append("\n");
            text.append(command.getDescription()).append("\n \n");
        }
        restChannel.createMessage(createEmbed(text.toString(), user)).block();
    }

    private EmbedData createEmbed(String commands, User user){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!help`")
                .description(commands)
                .color(GlobalColors.BLUE.getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(user.getAvatarUrl()).build())
                .build();
    }
}
