package ru.pinkgoosik.kitsun.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.api.MojangAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.config.Config;
import ru.pinkgoosik.kitsun.cosmetica.PlayerCloaks;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.perms.AccessManager;
import ru.pinkgoosik.kitsun.perms.Permissions;

public class RegisterCommand extends Command {

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String getDescription() {
        return "Registers a player to the list";
    }

    @Override
    public String appendName() {
        return "**" + Config.general.prefix + this.getName() + "** <nickname>";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String username = context.getFirstArgument();

        if (!AccessManager.hasAccessTo(member, Permissions.CLOAK_GRANT)) {
            channel.createMessage(createErrorEmbed("Not enough permissions.")).block();
            return;
        }

        if (username.equals("empty")) {
            channel.createMessage(createErrorEmbed("You have not specified a username!")).block();
            return;
        }

        String discordId = member.getId().asString();

        PlayerCloaks.ENTRIES.forEach(entry -> {
            if (!entry.user.name.equals(username)) {
                if (MojangAPI.getUuid(username).isPresent()) {
                    PlayerCloaks.register(discordId, username, MojangAPI.getUuid(username).get());
                    FtpConnection.updateData();
                    channel.createMessage(createSuccessfulEmbed("Player Registering", "Player " + username + " is now registered! \nPlease checkout !help for more commands")).block();
                } else channel.createMessage(createErrorEmbed("Player " + username + " is not found. Write down your Minecraft username.")).block();
            } else {
                channel.createMessage(createErrorEmbed("Player " + username + " is already registered!")).block();
            }
        });
    }

}
