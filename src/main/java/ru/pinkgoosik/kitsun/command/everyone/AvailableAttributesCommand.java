package ru.pinkgoosik.kitsun.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.cosmetica.PlayerCloaks;
import ru.pinkgoosik.kitsun.perms.AccessManager;
import ru.pinkgoosik.kitsun.perms.Permissions;

public class AvailableAttributesCommand extends Command {

    @Override
    public String getName() {
        return "available attributes";
    }

    @Override
    public String getDescription() {
        return "Sends list of available attributes for use.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();

        if(!AccessManager.hasAccessTo(member, Permissions.AVAILABLE_ATTRIBUTES)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }
        StringBuilder text = new StringBuilder();
        for (String cloak : PlayerCloaks.ATTRIBUTES){
            text.append(cloak).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        channel.createMessage(createInfoEmbed("Available Attributes", respond)).block();
    }
}
