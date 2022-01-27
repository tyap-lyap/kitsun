package ru.pinkgoosik.kitsun.command.everyone;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.api.MojangAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class RegisterCommand extends Command {

    @Override
    public String getName() {
        return "register";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"reg"};
    }

    @Override
    public String getDescription() {
        return "Registers a player to the system.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <nickname>";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String username = context.getFirstArg();
        String discordId = member.getId().asString();
        AccessManager accessManager = context.getServerData().accessManager;

        if(!accessManager.hasAccessTo(member, Permissions.REGISTER)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if(CosmeticaData.getEntry(discordId).isPresent()) {
            channel.createMessage(Embeds.error("You already registered!")).block();
            return;
        }

        if(username.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a username!")).block();
            return;
        }

        if(CosmeticaData.getEntryByName(username).isPresent()) {
            channel.createMessage(Embeds.error("Player " + username + " is already registered!")).block();
            return;
        }

        if(MojangAPI.getUuid(username).isPresent()) {
            CosmeticaData.register(discordId, username, MojangAPI.getUuid(username).get());
            FtpConnection.updateData();
            channel.createMessage(Embeds.success("Player Registering", "Player " + username + " is now registered! \nPlease checkout `!help` for more commands.")).block();
        }else channel.createMessage(Embeds.error("Player " + username + " is not found. Write down your Minecraft username.")).block();
    }
}
