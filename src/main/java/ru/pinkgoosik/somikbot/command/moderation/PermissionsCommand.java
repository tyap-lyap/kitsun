package ru.pinkgoosik.somikbot.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.discordjson.json.EmbedData;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.somikbot.command.Command;
import ru.pinkgoosik.somikbot.command.CommandUseContext;
import ru.pinkgoosik.somikbot.permissons.AccessManager;
import ru.pinkgoosik.somikbot.permissons.Permissions;
import ru.pinkgoosik.somikbot.util.GlobalColors;

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
