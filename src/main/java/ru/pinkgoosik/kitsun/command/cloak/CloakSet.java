package ru.pinkgoosik.kitsun.command.cloak;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class CloakSet extends Command {

    @Override
    public String getName() {
        return "cloak set";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"cape set", "cape grant", "clock grant", "cloack grant", "cock grant",
                "cape grand", "clock grand", "cloack grand", "cock grand", "clock set",
                "cloack set", "cock set"};
    }

    @Override
    public String getDescription() {
        return "Changes a player's cloak.";
    }

    @Override
    public String appendArgs() {
        return " <cloak>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if (disallowed(ctx, Permissions.CLOAK_SET)) return;
        String cloak = ctx.args.get(0).toLowerCase();

        if (cloak.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a cloak name!")).block();
            return;
        }
        if (!Cloaks.exist(cloak)) {
            ctx.channel.createMessage(Embeds.error("Cloak `" + cloak + "` is not found. Use `!cloaks` to see available cloaks.")).block();
            return;
        }
        CosmeticaData.getEntry(ctx.memberId).ifPresentOrElse(entry -> {
            CosmeticaData.setCloak(entry.user.name, cloak);
            FtpConnection.updateData();
            String text = "You successfully changed your cloak to `" + cloak + "`." + "\nPlease rejoin the world or server to see changes.";
            ctx.channel.createMessage(Embeds.success("Cloak Changing", text, Cloaks.PREVIEW_CLOAK.replace("%cloak%", cloak))).block();

        }, () -> ctx.channel.createMessage(Embeds.error("You have not registered yet!")).block());
    }
}
