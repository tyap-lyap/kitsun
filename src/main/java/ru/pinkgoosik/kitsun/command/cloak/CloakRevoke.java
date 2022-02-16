package ru.pinkgoosik.kitsun.command.cloak;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class CloakRevoke extends Command {

    @Override
    public String getName() {
        return "cloak revoke";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"cape clear", "cape revoke", "cloak clear", "clock revoke", "cloack revoke",
                "cock revoke", "cloak revok", "cape revok", "clock revok", "cloack revok", "cock revok"};
    }

    @Override
    public String getDescription() {
        return "Revokes a cloak from the player.";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if (disallowed(ctx, Permissions.CLOAK_REVOKE)) return;

        CosmeticaData.getEntry(ctx.memberId).ifPresentOrElse(entry -> {
            if(!CosmeticaData.hasCloak(entry.user.name)) {
                String text = "You can't revoke your cloak when you don't have one!";
                ctx.channel.createMessage(Embeds.error(text)).block();
            } else {
                CosmeticaData.clearCloak(entry.user.name);
                FtpConnection.updateData();
                String text = "Successfully revoked your cloak.";
                ctx.channel.createMessage(Embeds.success("Cloak Revoking", text)).block();
            }
        }, () -> ctx.channel.createMessage(Embeds.error("You have not registered yet!")).block());
    }
}
