package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedThumbnailData;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.permissons.Permissions;
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
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String nickname = context.getFirstArgument();
        String cloak = context.getSecondArgument();

        if (!AccessManager.hasAccessTo(member, Permissions.CLOAK_GRANT)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        if(nickname.equals("empty")){
            channel.createMessage(createErrorEmbed("Nickname is empty.")).block();
            return;
        }
        if(cloak.equals("empty")){
            channel.createMessage(createErrorEmbed("Cloak is empty.")).block();
            return;
        }
        if(PlayerCloaks.hasCloak(nickname)){
            channel.createMessage(createErrorEmbed(nickname + " already has a cloak.")).block();
            return;
        }
        if(!PlayerCloaks.CLOAKS.contains(cloak) || UuidGetter.getUuid(nickname) == null){
            channel.createMessage(createErrorEmbed("Cloak or Player not found.")).block();
            return;
        }

        PlayerCloaks.grantCloak(member.getId().asString(), nickname, UuidGetter.getUuid(nickname), cloak);
        FtpConnection.updateCapesData();
        String text = nickname + " got successfully granted with the " + cloak + " cloak." + "\nRejoin the world to see changes.";
        channel.createMessage(createSuccessEmbed(text, cloak, member)).block();
    }

    private EmbedData createSuccessEmbed(String text, String cloak, Member member){
        return EmbedData.builder()
                .title(member.getUsername() + " used command `!cloak grant`")
                .description(text)
                .color(GlobalColors.GREEN.getRGB())
                .thumbnail(EmbedThumbnailData.builder().url(PREVIEW_CLOAK.replace("%cloak%", cloak)).build())
                .build();
    }
}
