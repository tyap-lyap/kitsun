package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.feature.FtpConnection;
import ru.pinkgoosik.somikbot.permissons.Permissions;

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
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String nickname = context.getFirstArgument();
        String discordId = member.getId().asString();

        if(nickname.equals("empty")){
            channel.createMessage(createErrorEmbed("You have not specified a nickname!")).block();
            return;
        }
        if(!PlayerCloaks.hasCloak(nickname)){
            channel.createMessage(createErrorEmbed(nickname + " doesn't have a cloak.")).block();
            return;
        }

        for(var entry : PlayerCloaks.ENTRIES){
            if(entry.name().equals(nickname)){
                if(entry.discord().equals(discordId)){
                    if (AccessManager.hasAccessTo(member, Permissions.CLOAK_REVOKE_SELF)){
                        PlayerCloaks.revokeCloak(nickname);
                        FtpConnection.updateCapesData();
                        String text = "Successfully revoked a cloak from the player " + nickname + ".";
                        channel.createMessage(createSuccessfulEmbed("Cloak Revoking", text)).block();
                    }else {
                        channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
                        return;
                    }
                }else {
                    if (AccessManager.hasAccessTo(member, Permissions.CLOAK_REVOKE_OTHER)){
                        PlayerCloaks.revokeCloak(nickname);
                        FtpConnection.updateCapesData();
                        String text = "Successfully revoked a cloak from the player " + nickname + ".";
                        channel.createMessage(createSuccessfulEmbed("Cloak Revoking", text)).block();
                    }else {
                        channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
                        return;
                    }
                }
                return;
            }
        }
    }
}
