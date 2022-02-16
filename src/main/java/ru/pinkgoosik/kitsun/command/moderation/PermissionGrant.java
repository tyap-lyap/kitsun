package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class PermissionGrant extends Command {

    @Override
    public String getName() {
        return "permission grant";
    }

    @Override
    public String getDescription() {
        return "Grants a permission to the role.";
    }

    @Override
    public String appendArgs() {
        return " <role id> <permission>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        String roleId = ctx.args.get(0);
        String permission = ctx.args.get(1);
        if (disallowed(ctx, Permissions.PERMISSION_GRANT)) return;

        if (!Permissions.LIST.contains(permission)) {
            ctx.channel.createMessage(Embeds.error("Such permission doesn't exist.")).block();
            return;
        }
        ctx.accessManager.grant(roleId, permission);
        String text = "`" + roleId + "` successfully granted with the `" + permission + "` permission.";
        ctx.channel.createMessage(Embeds.success("Permission Grating", text)).block();
    }
}
