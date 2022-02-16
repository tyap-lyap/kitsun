package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;
import ru.pinkgoosik.kitsun.util.ServerUtils;

public class PublisherAdd extends Command {

    @Override
    public String getName() {
        return "publisher add";
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
        if (!ServerUtils.hasChannel(ctx.serverData.serverId, channelIdArg)) {
            ctx.channel.createMessage(Embeds.error("Such channel doesn't exist!")).block();
            return;
        }
        ModrinthAPI.getProject(slug).ifPresentOrElse(project -> {
            for (var publisher : ctx.serverData.publishers) {
                if(publisher.channel.equals(channelIdArg) && publisher.project.equals(project.id)) {
                    ctx.channel.createMessage(Embeds.error("This channel already has a publisher of the `" + slug + "` project.")).block();
                    return;
                }
            }
            ctx.serverData.publishers.add(new ChangelogPublisher(ctx.serverData.serverId, channelIdArg, project.id));
            ctx.serverData.saveData();

            String text = "Changelog publisher for the project `" + slug + "` got successfully created!";
            ctx.channel.createMessage(Embeds.success("Creating Changelog Publisher", text)).block();

        }, () -> ctx.channel.createMessage(Embeds.error("Project `" + slug + "` is not found.")).block());
    }
}
