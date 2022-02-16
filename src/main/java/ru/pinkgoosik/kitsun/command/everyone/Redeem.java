package ru.pinkgoosik.kitsun.command.everyone;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.Codes;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class Redeem extends Command {

    @Override
    public String getName() {
        return "redeem";
    }

    @Override
    public String getDescription() {
        return "Redeems a cloak/cosmetic/attribute using a special code.";
    }

    @Override
    public String appendName(ServerConfig config) {
        return super.appendName(config) + " <code>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        String codeArg = ctx.args.get(0);

        if (disallowed(ctx, Permissions.REDEEM)) return;

        if(codeArg.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a code!")).block();
            return;
        }

        CosmeticaData.getEntry(ctx.memberId).ifPresent(entry -> Codes.getCode(codeArg).ifPresent(code -> {
            String username = entry.user.name;
            String text = "";
            if (code.type.equals("preview/cloak")) {
                entry.cloak.name = code.cloak.name;
                text = String.format("%s have successfully redeemed a cloak", username);
            }
            if (code.type.equals("cosmetic")) {
                entry.cosmetics.addAll(code.cosmetics);
                FtpConnection.updateData();
                text = String.format("%s have successfully redeemed %s", username, code.cosmetics.size() > 1 ? "some cosmetics": "a cosmetic");
            }
            if (code.type.equals("attribute")) {
                entry.attributes.addAll(code.attributes);
                FtpConnection.updateData();
                text = String.format("%s have successfully redeemed %s", username, code.attributes.size() > 1 ? "some attributes": "an attribute");
            }
            ctx.channel.createMessage(Embeds.success("Redeeming code", text)).block();
        }));
    }
}
