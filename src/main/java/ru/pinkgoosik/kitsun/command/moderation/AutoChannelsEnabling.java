package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

public class AutoChannelsEnabling {

    public static Command enable() {
        return CommandBuilder.create("auto-channels enable")
                .description("Enables auto-channels and links it to the specific voice channel.")
                .args("<channel id>")
                .requires(Permissions.AUTO_CHANNELS_ENABLE)
                .respond(ctx -> {
                    String channelIdArg = ctx.args.get(0);
                    if (ctx.serverData.autoChannels.get().enabled) {
                        ctx.channel.createMessage(Embeds.error("Auto channels are already enabled!")).block();
                        return;
                    }
                    if (channelIdArg.equals("empty")) {
                        ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
                        return;
                    }
                    if (!ChannelUtils.hasChannel(ctx.serverData.server, channelIdArg)) {
                        ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
                        return;
                    }
                    if(!ChannelUtils.isVoiceChannel(ctx.serverData.server, channelIdArg)) {
                        ctx.channel.createMessage(Embeds.error("A channel you specified isn't a voice channel!")).block();
                        return;
                    }
                    ctx.serverData.autoChannels.get().enable(channelIdArg);
                    ctx.serverData.autoChannels.save();
                    ctx.channel.createMessage(Embeds.success("Auto Channels Enabling", "Auto channels are now enabled!")).block();
                }).build();
    }

    public static Command disable() {
        return CommandBuilder.create("auto-channels disable")
                .description("Disables auto channels.")
                .requires(Permissions.AUTO_CHANNELS_DISABLE)
                .respond(ctx -> {
                    if (!ctx.serverData.autoChannels.get().enabled) {
                        ctx.channel.createMessage(Embeds.error("Auto channels are already disabled!")).block();
                    }
                    else {
                        ctx.serverData.autoChannels.get().disable();
                        ctx.serverData.autoChannels.save();
                        ctx.channel.createMessage(Embeds.success("Auto Channels Disabling", "Auto channels are now disabled!")).block();
                    }
                }).build();
    }
}
