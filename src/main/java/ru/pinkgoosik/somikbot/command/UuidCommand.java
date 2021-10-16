package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.util.UuidGetter;

public class UuidCommand extends Command {

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public String getDescription() {
        return "Sends UUID of the Player.";
    }

    @Override
    public String appendName() {
        return "**!" + this.getName() + "** <nickname>";
    }

    @Override
    public void respond(MessageCreateEvent event, String[] args) {
        User user;
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        RestChannel restChannel;
        String nickname = args[1];

        if(event.getMessage().getAuthor().isEmpty()) return;
        else user = event.getMessage().getAuthor().get();
        if(channel == null) return;
        else restChannel = event.getClient().getRestClient().getChannelById(channel.getId());

        if(UuidGetter.getUuid(nickname) == null){
            restChannel.createMessage(createErrorEmbed("Player not found.")).block();
        }
        else {
            String text = nickname + "'s UUID: \n`" + UuidGetter.getUuid(nickname) + "`";
            restChannel.createMessage(createSuccessEmbed(text, user)).block();
        }
    }

    private EmbedData createSuccessEmbed(String text, User user){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!uuid`")
                .description(text)
                .color(Color.of(96,141,238).getRGB())
                .build();
    }
}
