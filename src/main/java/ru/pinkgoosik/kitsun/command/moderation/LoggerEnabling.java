package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class LoggerEnabling {

    public static Command enable() {
        return CommandBuilder.create("logger enable")
                .description("Enables logger and links it to the specific channel.")
                .args("<channel id>")
                .requires(Permissions.LOGGER_ENABLE)
                .respond(ctx -> {
                    String channelIdArg = ctx.args.get(0);
                    if (ctx.serverData.logger.enabled) {
                        ctx.channel.createMessage(Embeds.error("The logger is already enabled!")).block();
                        return;
                    }
                    if (channelIdArg.equals("empty")) {
                        ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
                        return;
                    }
                    if (ServerUtils.hasChannel(ctx.serverData.serverId, channelIdArg)) {
                        ctx.serverData.logger.enable(channelIdArg);
                        ctx.serverData.saveData();
                        ctx.channel.createMessage(Embeds.success("Logger Enabling", "The logger is now enabled!")).block();
                    }
                    else ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
                }).build();
    }

    public static Command disable() {
        return CommandBuilder.create("logger disable")
                .description("Disables logger.")
                .requires(Permissions.LOGGER_DISABLE)
                .respond(ctx -> {
                    if (!ctx.serverData.logger.enabled) {
                        ctx.channel.createMessage(Embeds.error("The logger is already disabled!")).block();
                    }else {
                        ctx.serverData.logger.disable();
                        ctx.serverData.saveData();
                        ctx.channel.createMessage(Embeds.success("Logger Disabling", "The logger is now disabled!")).block();
                    }
                }).build();
    }
}
