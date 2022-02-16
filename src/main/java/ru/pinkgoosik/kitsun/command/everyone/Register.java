package ru.pinkgoosik.kitsun.command.everyone;

import ru.pinkgoosik.kitsun.api.mojang.MojangAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class Register extends Command {

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
    public String appendArgs() {
        return " <nickname>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        String username = ctx.args.get(0).replaceAll("[^a-zA-Z0-9_]", "");

        if(disallowed(ctx, Permissions.REGISTER)) return;

        if(CosmeticaData.getEntry(ctx.memberId).isPresent()) {
            ctx.channel.createMessage(Embeds.error("You already registered!")).block();
            return;
        }
        if(username.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a username!")).block();
            return;
        }
        if(CosmeticaData.getEntryByName(username).isPresent()) {
            ctx.channel.createMessage(Embeds.error("Player `" + username + "` is already registered!")).block();
            return;
        }
        if(MojangAPI.getUuid(username).isPresent()) {
            CosmeticaData.register(ctx.memberId, username, MojangAPI.getUuid(username).get());
            FtpConnection.updateData();
            ctx.channel.createMessage(Embeds.success("Player Registering", "Player `" + username + "` is now registered! \nPlease checkout `!help` for more commands.")).block();
        }
        else ctx.channel.createMessage(Embeds.error("Player `" + username + "` is not found. Write down your Minecraft username.")).block();
    }
}
