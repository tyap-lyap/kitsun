package ru.pinkgoosik.somikbot.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.config.Config;
import ru.pinkgoosik.somikbot.cosmetica.PlayerCloaks;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.permissons.Permissions;

public class AvailableCosmeticsCommand extends Command {

    @Override
    public String getName() {
        return "available cosmetics";
    }

    @Override
    public String getDescription() {
        return "Sends list of available cosmetics for use.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();

        if(!AccessManager.hasAccessTo(member, Permissions.AVAILABLE_COSMETICS)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }
        StringBuilder text = new StringBuilder();
        for (String cloak : PlayerCloaks.COSMETICS){
            text.append(cloak).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        channel.createMessage(createInfoEmbed("Available Cosmetics", respond)).block();
    }
}
