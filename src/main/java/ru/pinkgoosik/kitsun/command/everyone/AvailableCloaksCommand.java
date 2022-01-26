package ru.pinkgoosik.kitsun.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.cosmetica.PlayerCloaks;
import ru.pinkgoosik.kitsun.perms.AccessManager;
import ru.pinkgoosik.kitsun.perms.Permissions;

public class AvailableCloaksCommand extends Command {

    @Override
    public String getName() {
        return "available cloaks";
    }

    @Override
    public String getDescription() {
        return "Sends list of all available cloaks for use.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();

        if(!AccessManager.hasAccessTo(member, Permissions.AVAILABLE_CLOAKS)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        StringBuilder text = new StringBuilder();
        for (String cloak : PlayerCloaks.COLORED_CLOAKS){
            text.append(cloak).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        channel.createMessage(createInfoEmbed("Available Colored Cloaks", respond)).block();

        StringBuilder text2 = new StringBuilder();
        for (String cosmetic : PlayerCloaks.PRIDE_CLOAKS){
            text2.append(cosmetic).append(", ");
        }
        String respond2 = text2.deleteCharAt(text2.length() - 1).deleteCharAt(text2.length() - 1).append(".").toString();
        channel.createMessage(createInfoEmbed("Available Pride Cloaks", respond2)).block();

        StringBuilder text3 = new StringBuilder();
        for (String attribute : PlayerCloaks.PATTERNED_CLOAKS){
            text3.append(attribute).append(", ");
        }
        String respond3 = text3.deleteCharAt(text3.length() - 1).deleteCharAt(text3.length() - 1).append(".").toString();
        channel.createMessage(createInfoEmbed("Available Patterned Cloaks", respond3)).block();
    }
}
