package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.instance.ServerData;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;
import ru.pinkgoosik.kitsun.instance.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class ChangelogPublisherRemoveCommand extends Command {

    @Override
    public String getName() {
        return "publisher remove";
    }

    @Override
    public String getDescription() {
        return "Removes changelog publisher of the certain Modrinth Project.";
    }

    @Override
    public String appendName(ServerConfig config) {
        return "**`" + config.general.commandPrefix + this.getName() + " <slug>`**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String slugArg = context.getFirstArg();
        ServerData serverData = context.getServerData();
        AccessManager accessManager = serverData.accessManager;

        if (!accessManager.hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_REMOVE)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (slugArg.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a project slug!")).block();
            return;
        }

        var project = ModrinthAPI.getProject(slugArg);

        if(project.isEmpty()) {
            channel.createMessage(Embeds.error("Project `" + slugArg + "` is not found.")).block();
            return;
        }

        if (hasPublishers(project.get().id, serverData)) {
            serverData.publishers.removeIf(publisher -> publisher.project.equals(project.get().id));
            serverData.saveData();
            String text = "All publisher of the `" + slugArg + "` project got removed.";
            channel.createMessage(Embeds.success("Removing Changelog Publisher", text)).block();
        }else {
            channel.createMessage(Embeds.error("`" + slugArg + "` project doesn't have any publishers.")).block();
        }
    }

    private static boolean hasPublishers(String projectId, ServerData serverData) {
        for (var publisher : serverData.publishers) {
            if(publisher.project.equals(projectId)) return true;
        }
        return false;
    }
}
