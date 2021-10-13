package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
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
    public void respond(MessageCreateEvent event, User user, MessageChannel channel) {
        String msg = event.getMessage().getContent() + " empty";
        String[] args = msg.split(" ");
        RestChannel restChannel = event.getClient().getRestClient().getChannelById(channel.getId());

        if(UuidGetter.getUuid(args[1]) == null){
            restChannel.createMessage(createEmbed(user, "Player not found.", Color.of(246,129,129))).block();
        }
        else if(UuidGetter.getUuid(args[1]) != null) {
            String respond = args[1] + "'s UUID: \n`" + UuidGetter.getUuid(args[1]) + "`";
            restChannel.createMessage(createEmbed(user, respond, Color.of(96,141,238))).block();
        }
    }

    private EmbedData createEmbed(User user, String text, Color color){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!uuid`")
                .description(text)
                .color(color.getRGB())
                .build();
    }
}
