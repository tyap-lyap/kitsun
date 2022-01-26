package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.perms.AccessManager;
import ru.pinkgoosik.kitsun.perms.Permissions;
import ru.pinkgoosik.kitsun.util.GlobalColors;

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
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "** <role id> <permission>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String roleId = context.getFirstArgument();
        String permission = context.getSecondArgument();

        if (!AccessManager.hasAccessTo(member, Permissions.PERMISSION_GRANT)) {
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }
        if (!Permissions.LIST.contains(permission)) {
            channel.createMessage(createErrorEmbed("Such permission doesn't exist.")).block();
        }

        AccessManager.grant(roleId, true, permission);
        String text = roleId + " successfully granted with the " + permission + " permission.";
        channel.createMessage(createEmbed(text, member)).block();
    }

    private EmbedData createEmbed(String text, Member member){
        return EmbedData.builder()
                .title(member.getUsername() + " used command `!permission grant`")
                .description(text)
                .color(GlobalColors.BLUE.getRGB())
                .build();
    }
}
