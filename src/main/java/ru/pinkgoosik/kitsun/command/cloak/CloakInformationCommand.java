package ru.pinkgoosik.kitsun.command.cloak;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.cosmetica.Entry;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Optional;

public class CloakInformationCommand extends Command {

    @Override
    public String getName() {
        return "info cloak";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"info cape", "info cap", "info clock", "info cloack", "info clok", "info cock",
                "inf cloak", "inf cape", "inf cap", "inf clock", "inf cloack", "inf clok", "inf cock"};
    }

    @Override
    public String getDescription() {
        return "Tells the player information about their current cloak.";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String discordId = member.getId().asString();
        AccessManager accessManager = context.getAccessManager();

        if (!accessManager.hasAccessTo(member, Permissions.CLOAK_INFORMATION)) {
            channel.createMessage(Embeds.error("Not enough permissions for this command.")).block();
            return;
        }
        Optional<Entry> entry = CosmeticaData.getEntry(discordId);
        if(entry.isPresent()) {
            String text = "Your current cape is " + entry.get().cloak.name + ".";
            channel.createMessage(Embeds.success("Information", text, Cloaks.PREVIEW_CLOAK.replace("%cloak%", entry.get().cloak.name))).block();
        }else {
            channel.createMessage(Embeds.error("User not found.")).block();
        }
    }
}
