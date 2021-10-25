package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCapes;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.util.GlobalColors;
import ru.pinkgoosik.somikbot.util.UuidGetter;

public class CloakGrantCommand extends Command {
    private static final String PREVIEW_CLOAK = "https://raw.githubusercontent.com/PinkGoosik/somik-bot/master/src/main/resources/cloak/%cloak%_cloak_preview.png";

    @Override
    public String getName() {
        return "cloak grant";
    }

    @Override
    public String getDescription() {
        return "Grants a cloak to the player.";
    }

    @Override
    public String appendName() {
        return "**!" + this.getName() + "** <nickname> <cloak>";
    }

    @Override
    public void respond(MessageCreateEvent event, String[] args) {
        User user;
        Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();
        RestChannel restChannel;
        String nickname = args[1];
        String cloak = args[2];

        if(event.getMessage().getAuthor().isEmpty()) return;
        else user = event.getMessage().getAuthor().get();
        if(channel == null) return;
        else restChannel = event.getClient().getRestClient().getChannelById(channel.getId());

        if(nickname.equals("empty")){
            restChannel.createMessage(createErrorEmbed("Nickname is empty.")).block();
            return;
        }
        if(cloak.equals("empty")){
            restChannel.createMessage(createErrorEmbed("Cloak is empty.")).block();
            return;
        }
        if(PlayerCapes.hasCape(nickname)){
            restChannel.createMessage(createErrorEmbed(nickname + " already has a cloak.")).block();
            return;
        }
        if(!PlayerCapes.CAPES.contains(cloak) || UuidGetter.getUuid(nickname) == null){
            restChannel.createMessage(createErrorEmbed("Cloak or Player not found.")).block();
            return;
        }

        PlayerCapes.grantCape(user.getId().asString(), nickname, UuidGetter.getUuid(nickname), cloak);
        FtpConnection.updateCapesData();
        String text = nickname + " got successfully granted with the " + cloak + " cloak." + "\nRejoin the world to see changes.";
        restChannel.createMessage(createSuccessEmbed(text, cloak, user)).block();
    }

    private EmbedData createSuccessEmbed(String text, String cloak, User user){
        return EmbedData.builder()
                .title(user.getUsername() + " used command `!cloak grant`")
                .description(text)
                .color(GlobalColors.GREEN.getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(PREVIEW_CLOAK.replace("%cloak%", cloak)).build())
                .build();
    }
}
