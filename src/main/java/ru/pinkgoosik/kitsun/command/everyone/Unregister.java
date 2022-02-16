package ru.pinkgoosik.kitsun.command.everyone;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class Unregister extends Command {

    @Override
    public String getName() {
        return "unregister";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"unreg"};
    }

    @Override
    public String getDescription() {
        return "Unregisters a player from the system.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if(disallowed(ctx, Permissions.UNREGISTER)) return;

        CosmeticaData.getEntry(ctx.memberId).ifPresentOrElse(entry -> {
            CosmeticaData.unregister(ctx.memberId);
            FtpConnection.updateData();
            String text = "Player " + entry.user.name + " is successfully unregistered. \nHope to see you soon later!";
            ctx.channel.createMessage(Embeds.success("Player Unregistering", text)).block();
        }, () -> ctx.channel.createMessage(Embeds.error("You have not registered yet!")).block());
    }
}
