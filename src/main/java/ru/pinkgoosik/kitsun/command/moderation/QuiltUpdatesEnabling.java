package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandBuilder;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class QuiltUpdatesEnabling {

    public static Command enable() {
        return CommandBuilder.create("quilt-updates enable")
                .description("Enables Quilt Loader updates publishing and links it to the specific channel.")
                .args("<channel id>")
                .requires(Permissions.QUILT_UPDATES_ENABLE)
                .respond(ctx -> {
                    String channelIdArg = ctx.args.get(0);
                    if (ctx.serverData.quiltUpdatesPublisher.enabled) {
                        ctx.channel.createMessage(Embeds.error("Quilt Loader updates publishing is already enabled!")).block();
                        return;
                    }
                    if (channelIdArg.equals("empty")) {
                        ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
                        return;
                    }
                    if (ServerUtils.hasChannel(ctx.serverData.server, channelIdArg)) {
                        ctx.serverData.quiltUpdatesPublisher.enable(channelIdArg);
                        ctx.serverData.save();
                        ctx.channel.createMessage(Embeds.success("Quilt Loader Updates", "Quilt Loader updates publishing is now enabled!")).block();
                    }
                    else ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
                }).build();
    }

    public static Command disable() {
        return CommandBuilder.create("quilt-updates disable")
                .description("Disables Quilt Loader updates publishing.")
                .requires(Permissions.QUILT_UPDATES_DISABLE)
                .respond(ctx -> {
                    if (!ctx.serverData.quiltUpdatesPublisher.enabled) {
                        ctx.channel.createMessage(Embeds.error("Quilt Loader updates publishing is already disabled!")).block();
                    }else {
                        ctx.serverData.quiltUpdatesPublisher.disable();
                        ctx.serverData.quiltUpdatesPublisher.latestVersion = "";
                        ctx.serverData.save();
                        ctx.channel.createMessage(Embeds.success("Quilt Loader Updates", "Quilt Loader updates publishing is now disabled!")).block();
                    }
                }).build();
    }

}
