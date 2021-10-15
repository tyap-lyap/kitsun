package ru.pinkgoosik.somikbot.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.util.Color;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.util.UuidGetter;

public class CloakGrantCommand extends Command {

    private static final String PREVIEW_CLOAK = "https://raw.githubusercontent.com/PinkGoosik/somik-bot/master/src/main/resources/cloak/%cloak%_cloak_preview.png";

    @Override
    public String getName() {
        return "cloak";
    }

    @Override
    public String getDescription() {
        return "Gives and removes the cloak.";
    }

    @Override
    public String appendName() {
        return "**!" + this.getName() + "** <grant|revoke> <nickname> [cloak]";
    }

    @Override
    public void respond(MessageCreateEvent event, User user, MessageChannel channel) {
        String msg = event.getMessage().getContent() + " empty empty empty";
        String[] args = msg.split(" ");
        RestChannel restChannel = event.getClient().getRestClient().getChannelById(channel.getId());

        if(args[1].equals("revoke")) tryToRevoke(args[2], restChannel, user);
        if(args[1].equals("grant")) tryToGrant(args[2], args[3], restChannel, user);
    }

    private void tryToGrant(String nickname, String cape, RestChannel restChannel, User user){
        if(PlayerCapes.hasCape(nickname)){
            restChannel.createMessage(createErrorEmbed(nickname + " already has a cloak.")).block();
        }
        else if(!PlayerCapes.CAPES.contains(cape) || UuidGetter.getUuid(nickname) == null){
            restChannel.createMessage(createErrorEmbed("Cloak or Player not found")).block();
        }
        else {
            PlayerCapes.grantCape(user.getId().asString(), nickname, UuidGetter.getUuid(nickname), cape);
            FtpConnection.updateCapesData();
            String text = nickname + " got successfully granted with the " + cape + " cloak" + "\nRejoin the world to see changes.";
            restChannel.createMessage(createGrantEmbed(text, cape, nickname)).block();
        }
    }

    private void tryToRevoke(String nickname, RestChannel restChannel, User user){
        String discordId = user.getId().asString();

        PlayerCapes.entries.forEach(entry -> {
            if(entry.name().equals(nickname)){
                if(entry.id().equals(discordId)){
                    PlayerCapes.revokeCape(nickname);
                    FtpConnection.updateCapesData();
                    String text = "Successfully revoked a cloak from the player " + nickname + ".";
                    restChannel.createMessage(createRevokeEmbed(text, nickname)).block();
                }else {
                    String text = "You can't revoke a cloak from the player " + nickname + ".";
                    restChannel.createMessage(createErrorEmbed(text)).block();
                }
            }
        });

        if(!PlayerCapes.hasCape(nickname)){
            restChannel.createMessage(createErrorEmbed(nickname + " doesn't have a cloak.")).block();
        }

//        if(PlayerCapes.hasCape(nickname)){
//            PlayerCapes.revokeCape(nickname);
//            FtpConnection.updateCapesData();
//            String text = "Successfully revoked a cloak from the player " + nickname + ".";
//            restChannel.createMessage(createRevokeEmbed(text, nickname)).block();
//        }
//        else restChannel.createMessage(createErrorEmbed(nickname + " doesn't have a cloak.")).block();
    }

    private EmbedData createGrantEmbed(String text, String cloak, String user){
        return EmbedData.builder()
                .title(user + " used command `!cloak`")
                .description(text)
                .color(Color.of(145,219,105).getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(PREVIEW_CLOAK.replace("%cloak%", cloak)).build())
                .build();
    }

    private EmbedData createRevokeEmbed(String text, String user){
        return EmbedData.builder()
                .title(user + " used command `!cloak`")
                .description(text)
                .color(Color.of(145,219,105).getRGB())
                .build();
    }

    private EmbedData createErrorEmbed(String text){
        return EmbedData.builder()
                .title("Error")
                .description(text)
                .color(Color.of(246,129,129).getRGB())
                .build();
    }
}
