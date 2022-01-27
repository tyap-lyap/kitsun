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
        return new String[]{"cape revoke", "clock revoke", "cloack revoke", "cock revoke", "cloak revok",
                "cape revok", "clock revok", "cloack revok", "cock revok"};
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
        Optional<Entry> entry = CosmeticaData.getEntry(discordId);
        AccessManager accessManager = context.getServerData().accessManager;

        if(entry.isPresent()) {
            if (accessManager.hasAccessTo(member, Permissions.CLOAK_REVOKE_SELF)) {
                if(entry.get().cloak.name.isBlank()) {
                    channel.createMessage(Embeds.error("You can't revoke your cloak when you don't have one!")).block();
                }else {
                    CosmeticaData.clearCloak(entry.get().user.name);
                    FtpConnection.updateData();
                    String text = "Successfully revoked your cloak.";
                    channel.createMessage(Embeds.success("Cloak Revoking", text)).block();
                }
            } else {
                channel.createMessage(Embeds.error("Not enough permissions.")).block();
            }
        }else {
            channel.createMessage(Embeds.error("You have not registered yet!")).block();
        }
    }
}
