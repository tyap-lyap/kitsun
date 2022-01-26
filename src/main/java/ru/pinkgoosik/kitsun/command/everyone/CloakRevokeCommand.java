package ru.pinkgoosik.kitsun.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.cosmetica.PlayerCloaks;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.perms.AccessManager;
import ru.pinkgoosik.kitsun.perms.Permissions;

import java.util.ArrayList;

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
        return "**" + Config.general.prefix + this.getName() + "**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String discordId = member.getId().asString();
        String username = getNicknames(discordId).get(0);

        if (username.equals("empty")) {
            channel.createMessage(createErrorEmbed("You have not specified a username!")).block();
            return;
        }

        if (!PlayerCloaks.hasCloak(username)) {
            channel.createMessage(createErrorEmbed("You can't revoke your cape when you don't have one")).block();
            return;
        }

        for (var entry : PlayerCloaks.ENTRIES) {
            if (entry.user.name.equals(username)) {
                if (entry.user.discord.equals(discordId)) {
                    if (AccessManager.hasAccessTo(member, Permissions.CLOAK_REVOKE_SELF)) {
                        PlayerCloaks.revokeCloak(username);
                        FtpConnection.updateData();
                        String text = "Successfully revoked your cloak.";
                        channel.createMessage(createSuccessfulEmbed("Cloak Revoking", text)).block();
                    } else {
                        channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
                    }
                } else {
                    if (AccessManager.hasAccessTo(member, Permissions.CLOAK_REVOKE_OTHER)) {
                        PlayerCloaks.revokeCloak(username);
                        FtpConnection.updateData();
                        String text = String.format("Successfully revoked %s's cloak.", username);
                        channel.createMessage(createSuccessfulEmbed("Cloak Revoking", text)).block();
                    } else {
                        channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
                    }
                }
                return;
            }
        }
    }

    private static ArrayList<String> getNicknames(String discord) {
        ArrayList<String> nicknames = new ArrayList<>();
        for (var entry : PlayerCloaks.ENTRIES) {
            if (entry.user.discord.equals(discord)) nicknames.add(entry.user.name);
        }
        return nicknames;
    }
}
