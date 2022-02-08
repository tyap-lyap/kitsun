package ru.pinkgoosik.kitsun.command.cloak;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.cosmetica.Entry;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Optional;

public class CloakRevokeCommand extends Command {

    @Override
    public String getName() {
        return "cloak revoke";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"cape clear", "cape revoke", "cloak clear", "clock revoke", "cloack revoke",
                "cock revoke", "cloak revok", "cape revok", "clock revok", "cloack revok", "cock revok"};
    }

    @Override
    public String getDescription() {
        return "Revokes a cloak from the player.";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String discordId = member.getId().asString();
        AccessManager accessManager = context.getServerData().accessManager;

        String username;
        Optional<Entry> entry = CosmeticaData.getEntry(discordId);

        if(entry.isPresent()) {
            username = entry.get().user.name;
        }else {
            channel.createMessage(Embeds.error("You have not registered yet!")).block();
            return;
        }

        if (!accessManager.hasAccessTo(member, Permissions.CLOAK_REVOKE_SELF)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if(!CosmeticaData.hasCloak(username)) {
            String text = "You can't revoke your cloak when you don't have one!";
            channel.createMessage(Embeds.error(text)).block();
            return;
        }

        CosmeticaData.clearCloak(username);
        FtpConnection.updateData();
        String text = "Successfully revoked your cloak.";
        channel.createMessage(Embeds.success("Cloak Revoking", text)).block();
    }
}
