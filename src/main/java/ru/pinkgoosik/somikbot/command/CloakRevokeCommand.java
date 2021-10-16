package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.feature.FtpConnection;

public class CloakRevokeCommand extends Command {

    @Override
    public String getName() {
        return "cloak revoke";
    }

    @Override
    public String getDescription() {
        return "Revokes a cloak from the player.";
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

        String discordId = user.getId().asString();

        if(nickname.equals("empty")){
            restChannel.createMessage(createErrorEmbed("Nickname is empty.")).block();
            return;
        }
        if(!PlayerCapes.hasCape(nickname)){
            restChannel.createMessage(createErrorEmbed(nickname + " doesn't have a cloak.")).block();
            return;
        }

        for(var entry : PlayerCapes.entries){
            if(entry.name().equals(nickname)){
                if(entry.id().equals(discordId)){
                    PlayerCapes.revokeCape(nickname);
                    FtpConnection.updateCapesData();
                    String text = "Successfully revoked a cloak from the player " + nickname + ".";
                    restChannel.createMessage(createSuccessEmbed(text, user)).block();
                }else {
                    String text = "You can't revoke a cloak from the player " + nickname + ".";
                    restChannel.createMessage(createErrorEmbed(text)).block();
                }
                return;
            }
        }
    }

    private EmbedData createSuccessEmbed(String text, User user){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!cloak revoke`")
                .description(text)
                .color(Color.of(145,219,105).getRGB())
                .build();
    }
}
