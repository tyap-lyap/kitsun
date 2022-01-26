package ru.pinkgoosik.kitsun.command.cosmetics;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Attributes;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class AvailableAttributesCommand extends Command {

    @Override
    public String getName() {
        return "attributes";
    }

    @Override
    public String getDescription() {
        return "Sends list of available attributes for use.";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        AccessManager accessManager = context.getAccessManager();

        if(!accessManager.hasAccessTo(member, Permissions.AVAILABLE_ATTRIBUTES)){
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }
        StringBuilder text = new StringBuilder();
        for (String cloak : Attributes.ATTRIBUTES) {
            text.append(cloak).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        channel.createMessage(Embeds.info("Available Attributes", respond)).block();
    }
}
