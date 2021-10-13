package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;

public class CapesCommand extends Command {

    @Override
    public String getName() {
        return "capes";
    }

    @Override
    public String getDescription() {
        return "Sends list of available capes.";
    }

    @Override
    public void respond(MessageCreateEvent event, User user, MessageChannel channel) {
        RestChannel restChannel = event.getClient().getRestClient().getChannelById(channel.getId());
        StringBuilder string = new StringBuilder();
        string.append("**Available Capes**\n");
        for (String cape : PlayerCapes.CAPES){
            string.append(cape).append(", ");
        }
        String respond = string.deleteCharAt(string.length() - 1).deleteCharAt(string.length() - 1).append(".").toString();
        restChannel.createMessage(createEmbed(user, respond)).block();
    }

    private EmbedData createEmbed(User user, String text){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!capes`")
                .description(text)
                .color(Color.of(96,141,238).getRGB())
                .build();
    }
}
