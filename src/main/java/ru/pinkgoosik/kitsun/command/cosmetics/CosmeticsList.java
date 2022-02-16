package ru.pinkgoosik.kitsun.command.cosmetics;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Cosmetics;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class CosmeticsList extends Command {

    @Override
    public String getName() {
        return "cosmetics";
    }

    @Override
    public String getDescription() {
        return "Sends list of available cosmetics for use.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if(disallowed(ctx, Permissions.AVAILABLE_COSMETICS)) return;

        StringBuilder text = new StringBuilder();
        for (String cloak : Cosmetics.COSMETICS) {
            text.append(cloak).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        ctx.channel.createMessage(Embeds.info("Available Cosmetics", respond)).block();
    }
}
