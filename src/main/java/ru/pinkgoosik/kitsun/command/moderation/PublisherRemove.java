package ru.pinkgoosik.kitsun.command.moderation;

import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.instance.ServerData;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class PublisherRemove extends Command {

    @Override
    public String getName() {
        return "mod-updates-publisher remove";
    }

    @Override
    public String getDescription() {
        return "Removes changelog publisher of the certain Modrinth Project.";
    }

    @Override
    public String appendArgs() {
        return " <slug>";
    }

    @Override
    public void respond(CommandUseContext ctx) {
        String slugArg = ctx.args.get(0);
        if (disallowed(ctx, Permissions.CHANGELOG_PUBLISHER_REMOVE)) return;

        if (slugArg.equals("empty")) {
            ctx.channel.createMessage(Embeds.error("You have not specified a project slug!")).block();
            return;
        }
        ModrinthAPI.getProject(slugArg).ifPresentOrElse(project -> {
            if (hasPublishers(project.id, ctx.serverData)) {
                ctx.serverData.publishers.removeIf(publisher -> publisher.project.equals(project.id));
                ctx.serverData.save();
                String text = "All publisher of the `" + slugArg + "` project got removed.";
                ctx.channel.createMessage(Embeds.success("Removing Changelog Publisher", text)).block();
            }else {
                ctx.channel.createMessage(Embeds.error("`" + slugArg + "` project doesn't have any publishers.")).block();
            }
        }, () -> ctx.channel.createMessage(Embeds.error("Project `" + slugArg + "` is not found.")).block());
    }

    private static boolean hasPublishers(String projectId, ServerData serverData) {
        for (var publisher : serverData.publishers) {
            if(publisher.project.equals(projectId)) return true;
        }
        return false;
    }

}
