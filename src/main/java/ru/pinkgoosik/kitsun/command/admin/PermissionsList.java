package ru.pinkgoosik.kitsun.command.admin;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class PermissionsList extends Command {

    @Override
    public String getName() {
        return "permissions";
    }

    @Override
    public String getDescription() {
        return "Sends list of permissions.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if (disallowed(ctx, Permissions.PERMISSIONS)) return;

        StringBuilder text = new StringBuilder();
        for (String permission : Permissions.LIST) {
            text.append(permission).append("\n");
        }
        ctx.channel.createMessage(Embeds.info("Available Permissions", text.toString())).block();
    }
}
