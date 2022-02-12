package ru.pinkgoosik.kitsun.command.cloak;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.cosmetica.CosmeticaData;
import ru.pinkgoosik.kitsun.cosmetica.Entry;
import ru.pinkgoosik.kitsun.feature.FtpConnection;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;
import ru.pinkgoosik.kitsun.cosmetica.Cloaks;
import ru.pinkgoosik.kitsun.instance.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Optional;

public class CloakSetCommand extends Command {

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
    public String appendName(ServerConfig config) {
        String name = "**`" + config.general.commandPrefix + this.getName();
        return name + " <cloak>`** or **`" + config.general.commandPrefix + "cape set <cloak>`**";
    }

    @Override
    public void respond(CommandUseContext context) {
        RestChannel channel = context.getChannel();
        Member member = context.getMember();
        String cloak = context.getFirstArg().toLowerCase();
        AccessManager accessManager = context.getServerData().accessManager;

        String username;
        Optional<Entry> entry = CosmeticaData.getEntry(member.getId().asString());

        if(entry.isPresent()) {
            username = entry.get().user.name;
        }else {
            channel.createMessage(Embeds.error("You have not registered yet!")).block();
            return;
        }

        if (!accessManager.hasAccessTo(member, Permissions.CLOAK_SET)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (cloak.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a cloak name!")).block();
            return;
        }

        if (!Cloaks.COLORED_CLOAKS.contains(cloak) && !Cloaks.PATTERNED_CLOAKS.contains(cloak) && !Cloaks.PRIDE_CLOAKS.contains(cloak) && !Cloaks.FANCY_CLOAKS.contains(cloak)) {
            channel.createMessage(Embeds.error("Cloak `" + cloak + "` is not found. Use `!cloaks` to see available cloaks.")).block();
            return;
        }

        CosmeticaData.setCloak(username, cloak);
        FtpConnection.updateData();
        String text = "You successfully changed your cloak to `" + cloak + "`." + "\nPlease rejoin the world to see changes.";
        channel.createMessage(Embeds.success("Cloak Change", text, Cloaks.PREVIEW_CLOAK.replace("%cloak%", cloak))).block();
    }
}
