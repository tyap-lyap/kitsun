package ru.pinkgoosik.kitsun.command;

import ru.pinkgoosik.kitsun.config.ServerConfig;
import ru.pinkgoosik.kitsun.util.Embeds;

public abstract class Command {
    public abstract String getName();

    public String[] getAltNames() {
        return new String[]{""};
    }

    public abstract String getDescription();

    public String appendName(ServerConfig config) {
        String name = "**`" + config.general.commandPrefix + this.getName() + appendArgs() + "`**";

        if(!getAltNames()[0].isBlank()) {
            name = name + " or **`" + config.general.commandPrefix + getAltNames()[0] + appendArgs() + "`**";
        }
        return name;
    }

    public String appendArgs() {
        return "";
    }

    public void respond(CommandUseContext ctx) {}

    public boolean disallowed(CommandUseContext ctx, String permission) {
        if(!ctx.accessManager.hasAccessTo(ctx.member, permission)) {
            ctx.channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return true;
        }
        return false;
    }
}
