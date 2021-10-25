package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.util.GlobalColors;

public class CloaksCommand extends Command {

    @Override
    public String getName() {
        return "cloaks";
    }

    @Override
    public String getDescription() {
        return "Sends list of available cloaks.";
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
        text.append("**Available Cloaks**\n");
        for (String cape : PlayerCapes.CAPES){
            text.append(cape).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        restChannel.createMessage(createEmbed(respond, user)).block();
    }

    private EmbedData createEmbed(String text, User user){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!cloaks`")
                .description(text)
                .color(GlobalColors.BLUE.getRGB())
                .build();
    }
}
