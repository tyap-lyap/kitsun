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

public class PermissionsCommand extends Command {

    @Override
    public String getName() {
        return "permissions";
    }

    @Override
    public String getDescription() {
        return "Sends list of permissions.";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();

        if (!AccessManager.hasAccessTo(member, Permissions.PERMISSIONS)){
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }
        StringBuilder text = new StringBuilder();
        for (String permission : Permissions.LIST){
            text.append(permission).append("\n");
        }
        channel.createMessage(createEmbed(text.toString(), member)).block();
    }

    private EmbedData createEmbed(String text, Member member){
        return EmbedData.builder()
                .title(member.getUsername() + " used command `!permissions`")
                .description(text)
                .color(GlobalColors.BLUE.getRGB())
                .build();
    }
}
