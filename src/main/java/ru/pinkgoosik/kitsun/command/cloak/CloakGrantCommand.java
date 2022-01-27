package ru.pinkgoosik.kitsun.command.cloak;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.cosmetica.Entry;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Optional;

public class CloakGrantCommand extends Command {

    @Override
    public String getName() {
        return "cloak grant";
    }

    @Override
    public String[] getAltNames() {
        return new String[]{"cape grant", "clock grant", "cloack grant", "cock grant", "cloak gran",
                "cape gran", "clock gran", "cloack gran", "cock gran", "cape grand", "clock grand",
                "cloack grand", "cock grand"};
    }

    @Override
    public String getDescription() {
        return "Grants a cloak to the player.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <cloak>";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String cloak = context.getFirstArg();
        String discordId = member.getId().asString();
        AccessManager accessManager = context.getServerData().accessManager;
        String username;
        Optional<Entry> entry = CosmeticaData.getEntry(member.getId().asString());

        if(entry.isPresent()) {
            username = entry.get().user.name;
        }else {
            channel.createMessage(Embeds.error("User not found.")).block();
            return;
        }

        if (!accessManager.hasAccessTo(member, Permissions.CLOAK_GRANT)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (cloak.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a cloak name!")).block();
            return;
        }

        if (CosmeticaData.hasCloak(username)) {
            channel.createMessage(Embeds.error("You already has a cloak. Revoke it with the `!cloak revoke` command.")).block();
            return;
        }

        if (!Cloaks.COLORED_CLOAKS.contains(cloak) && !Cloaks.PATTERNED_CLOAKS.contains(cloak) && !Cloaks.PRIDE_CLOAKS.contains(cloak)) {
            channel.createMessage(Embeds.error("The " + cloak + " cloak is not found. Use `!available cloaks` to see available cloaks.")).block();
            return;
        }

        CosmeticaData.setCloak(discordId, cloak);
        FtpConnection.updateData();
        String text = "You successfully got granted the " + cloak + " cloak." + "\nPlease rejoin the world to see changes.";
        channel.createMessage(Embeds.success("Cloak Granting", text, Cloaks.PREVIEW_CLOAK.replace("%cloak%", cloak))).block();
    }
}
