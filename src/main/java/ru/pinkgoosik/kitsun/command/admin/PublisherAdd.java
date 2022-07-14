package ru.pinkgoosik.kitsun.command.admin;

import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.feature.ModUpdatesPublisher;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.ChannelUtils;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.ArrayList;
import java.util.List;

public class PublisherAdd extends Command {

    @Override
    public String getName() {
        return "mod-updates add";
    }

    @Override
    public String getDescription() {
        return "Creates changelog publisher of the certain Modrinth Project.";
    }

    @Override
    public String appendArgs() {
        return " <slug> <channel id>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        String slug = ctx.args.get(0);
        String channelIdArg = ctx.args.get(1);
        if (disallowed(ctx, Permissions.CHANGELOG_PUBLISHER_ADD)) return;

        if (slug.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a project slug!")).block();
            return;
        }
        if (channelIdArg.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
            return;
        }
        if (!ChannelUtils.exist(ctx.serverData.server, channelIdArg)) {
            ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
            return;
        }
        if(ChannelUtils.isVoiceChannel(ctx.serverData.server, channelIdArg)) {
            ctx.channel.createMessage(Embeds.error("You can't link publisher to a voice channel!")).block();
            return;
        }
        ModrinthAPI.getProject(slug).ifPresentOrElse(project -> {
            for (var publisher : ctx.serverData.publishers.get()) {
                if(publisher.channel.equals(channelIdArg) && publisher.project.equals(project.id)) {
                    ctx.channel.createMessage(Embeds.error("This channel already has a publisher of the `" + slug + "` project.")).block();
                    return;
                }
            }
            var old = ctx.serverData.publishers.get();
            var newOnes = new ArrayList<>(List.of(ctx.serverData.publishers.get()));
            newOnes.add(new ModUpdatesPublisher(ctx.serverData.server, channelIdArg, project.id));
            ctx.serverData.publishers.set(newOnes.toArray(old));
            ctx.serverData.publishers.save();

            String text = "Changelog publisher for the project `" + slug + "` got successfully created! Make sure bot has permission to send messages in this channel otherwise it wont work.";
            ctx.channel.createMessage(Embeds.success("Creating Changelog Publisher", text)).block();

        }, () -> ctx.channel.createMessage(Embeds.error("Project `" + slug + "` is not found.")).block());
    }
}
