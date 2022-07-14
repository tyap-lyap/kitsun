package ru.pinkgoosik.kitsun.command.admin;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

public class LoggerEnabling {

    public static Command enable() {
        return CommandBuilder.create("logger enable")
                .description("Enables logger and links it to the specific channel.")
                .args("<channel id>")
                .requires(Permissions.LOGGER_ENABLE)
                .respond(ctx -> {
                    String channelIdArg = ctx.args.get(0);
                    if(ctx.serverData.logger.get().enabled) {
                        ctx.channel.createMessage(Embeds.error("The logger is already enabled!")).block();
                        return;
                    }
                    if(channelIdArg.equals("empty")) {
                        ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
                        return;
                    }
                    if(!ChannelUtils.exist(ctx.serverData.server, channelIdArg)) {
                        ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
                        return;
                    }
                    if(ChannelUtils.isVoiceChannel(ctx.serverData.server, channelIdArg)) {
                        ctx.channel.createMessage(Embeds.error("You can't link logger to a voice channel!")).block();
                        return;
                    }
                    ctx.serverData.logger.get().enable(channelIdArg);
                    ctx.serverData.logger.save();
                    ctx.channel.createMessage(Embeds.success("Logger Enabling", "The logger is now enabled! Make sure bot has permission to send messages in this channel otherwise it wont work.")).block();
                }).build();
    }

    public static Command disable() {
        return CommandBuilder.create("logger disable")
                .description("Disables logger.")
                .requires(Permissions.LOGGER_DISABLE)
                .respond(ctx -> {
                    if (!ctx.serverData.logger.get().enabled) {
                        ctx.channel.createMessage(Embeds.error("The logger is already disabled!")).block();
                    }
                    else {
                        ctx.serverData.logger.get().disable();
                        ctx.serverData.logger.save();
                        ctx.channel.createMessage(Embeds.success("Logger Disabling", "The logger is now disabled!")).block();
                    }
                }).build();
    }
}
