package ru.pinkgoosik.kitsun.command.cloak;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class CloakInfo extends Command {

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
    public void respond(CommandUseContext ctx) {
        if (disallowed(ctx, Permissions.CLOAK_INFO)) return;

        CosmeticaData.getEntry(ctx.memberId).ifPresentOrElse(entry -> {
            if(!entry.cloak.name.isBlank()) {
                String text = "Your current cloak is `" + entry.cloak.name + "`.";
                ctx.channel.createMessage(Embeds.success("Information", text, Cloaks.PREVIEW_CLOAK.replace("%cloak%", entry.cloak.name))).block();
            }else {
                ctx.channel.createMessage(Embeds.success("Information", "You don't have one yet!")).block();
            }
        }, () -> ctx.channel.createMessage(Embeds.error("You have not registered yet!")).block());
    }
}
