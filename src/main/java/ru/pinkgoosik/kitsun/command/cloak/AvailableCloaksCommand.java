package ru.pinkgoosik.kitsun.command.cloak;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class AvailableCloaksCommand extends Command {

    @Override
    public String getName() {
        return "cloaks";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"capes", "caps", "clocks", "cloacks", "cloks", "cocks", "плащи", "пащи", "плащики"};
    }

    @Override
    public String getDescription() {
        return "Sends list of all available cloaks for use.";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        AccessManager accessManager = context.getAccessManager();

        if(!accessManager.hasAccessTo(member, Permissions.AVAILABLE_CLOAKS)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        StringBuilder textColored = new StringBuilder();
        for (String cloak : Cloaks.COLORED_CLOAKS) {
            textColored.append(cloak).append(", ");
        }
        String respondColored = textColored.deleteCharAt(textColored.length() - 1).deleteCharAt(textColored.length() - 1).append(".").toString();
        channel.createMessage(Embeds.info("Available Colored Cloaks", respondColored)).block();

        StringBuilder textPride = new StringBuilder();
        for (String cloak : Cloaks.PRIDE_CLOAKS) {
            textPride.append(cloak).append(", ");
        }
        String respondPride = textPride.deleteCharAt(textPride.length() - 1).deleteCharAt(textPride.length() - 1).append(".").toString();
        channel.createMessage(Embeds.info("Available Pride Cloaks", respondPride)).block();

        StringBuilder textFancy = new StringBuilder();
        for (String cloak : Cloaks.FANCY_CLOAKS) {
            textFancy.append(cloak).append(", ");
        }
        String respondFancy = textFancy.deleteCharAt(textFancy.length() - 1).deleteCharAt(textFancy.length() - 1).append(".").toString();
        channel.createMessage(Embeds.info("Available Fancy Cloaks", respondFancy)).block();

        StringBuilder textPatterned = new StringBuilder();
        for (String cloak : Cloaks.PATTERNED_CLOAKS) {
            textPatterned.append(cloak).append(", ");
        }
        String respondPatterned = textPatterned.deleteCharAt(textPatterned.length() - 1).deleteCharAt(textPatterned.length() - 1).append(".").toString();
        channel.createMessage(Embeds.info("Available Patterned Cloaks", respondPatterned)).block();
    }
}
