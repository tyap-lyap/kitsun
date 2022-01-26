package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class PermissionsCommand extends Command {

    @Override
    public String getName() {
        return "permissions";
    }

    @Override
    public String getDescription() {
        return "Sends list of permissions.";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        AccessManager accessManager = context.getAccessManager();

        if (!accessManager.hasAccessTo(member, Permissions.PERMISSIONS)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }
        StringBuilder text = new StringBuilder();
        for (String permission : Permissions.LIST){
            text.append(permission).append("\n");
        }
        channel.createMessage(Embeds.info("Available Permissions", text.toString())).block();
    }
}
