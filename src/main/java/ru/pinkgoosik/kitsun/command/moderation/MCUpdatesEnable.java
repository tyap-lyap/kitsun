package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class MCUpdatesEnable extends Command {

    @Override
    public String getName() {
        return "mcupdates enable";
    }

    @Override
    public String getDescription() {
        return "Enables Minecraft updates publishing and links it to the specific channel.";
    }

    @Override
    public String appendArgs() {
        return " <channel id>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        if (disallowed(ctx, Permissions.MCUPDATES_ENABLE)) return;

        String channelIdArg = ctx.args.get(0);

        if (ctx.serverData.mcUpdatesPublisher.enabled) {
            ctx.channel.createMessage(Embeds.error("The Minecraft updates publishing is already enabled!")).block();
            return;
        }
        if (channelIdArg.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
            return;
        }
        if (ServerUtils.hasChannel(ctx.serverData.serverId, channelIdArg)) {
            ctx.serverData.mcUpdatesPublisher.enable(channelIdArg);
            ctx.serverData.saveData();
            ctx.channel.createMessage(Embeds.success("Minecraft Updates Enabling", "The Minecraft Updates publishing is now enabled!")).block();
        }
        else ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
    }
}
