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

import static ru.pinkgoosik.kitsun.command.everyone.CloakGrantCommand.PREVIEW_CLOAK;

public class CloakChangeCommand extends Command {
    @Override
    public String getName() {
        return "cloak change";
    }

    @Override
    public String getDescription() {
        return "Changes a player's cloak";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "** <cloak>";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String cloak = context.getFirstArgument();
        String username = getNicknames(member.getId().asString()).get(0);

        if (!AccessManager.hasAccessTo(member, Permissions.CLOAK_CHANGE)) {
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        if (cloak.equals("empty")) {
            channel.createMessage(createErrorEmbed("You have not specified a cloak name!")).block();
            return;
        }

        if (!PlayerCloaks.COLORED_CLOAKS.contains(cloak) && !PlayerCloaks.PATTERNED_CLOAKS.contains(cloak) && !PlayerCloaks.PRIDE_CLOAKS.contains(cloak)) {
            channel.createMessage(createErrorEmbed("Cloak " + cloak + " is not found. Use `!available cloaks` to see available cloaks.")).block();
            return;
        }

        PlayerCloaks.editCloak(username, cloak);
        FtpConnection.updateData();
        String text = "You successfully changed your cloak to " + cloak + "." + "\nPlease rejoin the world to see changes.";
        channel.createMessage(createSuccessfulEmbed("Cloak Change", text, PREVIEW_CLOAK.replace("%cloak%", cloak))).block();
    }

    private static ArrayList<String> getNicknames(String discord) {
        ArrayList<String> nicknames = new ArrayList<>();
        for (var entry : PlayerCloaks.ENTRIES) {
            if (entry.user.discord.equals(discord)) nicknames.add(entry.user.name);
        }
        return nicknames;
    }
}
