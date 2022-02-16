package ru.pinkgoosik.kitsun.command.cosmetics;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Attributes;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class AttributesList extends Command {

    @Override
    public String getName() {
        return "attributes";
    }

    @Override
    public String getDescription() {
        return "Sends list of available attributes for use.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if(disallowed(ctx, Permissions.AVAILABLE_ATTRIBUTES)) return;

        StringBuilder text = new StringBuilder();
        for (String cloak : Attributes.ATTRIBUTES) {
            text.append(cloak).append(", ");
        }
        String respond = text.deleteCharAt(text.length() - 1).deleteCharAt(text.length() - 1).append(".").toString();
        ctx.channel.createMessage(Embeds.info("Available Attributes", respond)).block();
    }
}
