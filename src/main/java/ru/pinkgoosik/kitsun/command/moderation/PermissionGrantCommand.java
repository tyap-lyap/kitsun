package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class PermissionGrantCommand extends Command {

    @Override
    public String getName() {
        return "permission grant";
    }

    @Override
    public String getDescription() {
        return "Grants a permission to the role.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <role id> <permission>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String roleId = context.getFirstArg();
        String permission = context.getSecondArg();
        AccessManager accessManager = context.getServerData().accessManager;

        if (!accessManager.hasAccessTo(member, Permissions.PERMISSION_GRANT)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (!Permissions.LIST.contains(permission)) {
            channel.createMessage(Embeds.error("Such permission doesn't exist.")).block();
            return;
        }

        accessManager.grant(roleId, permission);
        String text = roleId + " successfully granted with the " + permission + " permission.";
        channel.createMessage(Embeds.success("Permission Grating", text)).block();
    }
}
