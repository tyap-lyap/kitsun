package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

public class MCUpdatesEnable extends Command {

    @Override
    public String getName() {
        return "mc-updates enable";
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
        if (disallowed(ctx, Permissions.MC_UPDATES_ENABLE)) return;

        String channelIdArg = ctx.args.get(0);

        if (ctx.serverData.mcUpdates.get().enabled) {
            ctx.channel.createMessage(Embeds.error("The Minecraft updates publishing is already enabled!")).block();
            return;
        }
        if (channelIdArg.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
            return;
        }
        if(!ChannelUtils.exist(ctx.serverData.server, channelIdArg)) {
            ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
            return;
        }
        if(ChannelUtils.isVoiceChannel(ctx.serverData.server, channelIdArg)) {
            ctx.channel.createMessage(Embeds.error("You can't link mc updates to a voice channel!")).block();
            return;
        }
        ctx.serverData.mcUpdates.get().enable(channelIdArg);
        ctx.serverData.mcUpdates.save();
        ctx.channel.createMessage(Embeds.success("Minecraft Updates Enabling", "The Minecraft updates publishing is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work.")).block();
    }
}
