package ru.pinkgoosik.kitsun.command.everyone;

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

public class UnregisterCommand extends Command {

    @Override
    public String getName() {
        return "unregister";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"unreg"};
    }

    @Override
    public String getDescription() {
        return "Unregisters a player from the system.";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String discordId = member.getId().asString();
        AccessManager accessManager = context.getServerData().accessManager;

        if(!accessManager.hasAccessTo(member, Permissions.UNREGISTER)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        String username;
        Optional<Entry> entry = CosmeticaData.getEntry(discordId);

        if(entry.isPresent()) {
            username = entry.get().user.name;
        }else {
            channel.createMessage(Embeds.error("You have not registered yet!")).block();
            return;
        }

        CosmeticaData.unregister(discordId);
        FtpConnection.updateData();
        String text = "Player " + username + " is successfully unregistered. \nHope to see you soon later!";
        channel.createMessage(Embeds.success("Player Unregistering", text)).block();
    }
}
