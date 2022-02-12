package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.feature.ChangelogPublisher;
import ru.pinkgoosik.kitsun.instance.ServerData;
import ru.pinkgoosik.kitsun.instance.config.ServerConfig;
import ru.pinkgoosik.kitsun.instance.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

public class ChangelogPublisherAddCommand extends Command {

    @Override
    public String getName() {
        return "publisher add";
    }

    @Override
    public String getDescription() {
        return "Creates changelog publisher of the certain Modrinth Project.";
    }

    @Override
    public String appendName(ServerConfig config) {
        return "**`" + config.general.commandPrefix + this.getName() + " <slug> <channel id>`**";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String slug = context.getFirstArg();
        String channelID = context.getSecondArg();
        ServerData serverData = context.getServerData();
        AccessManager accessManager = serverData.accessManager;

        if (!accessManager.hasAccessTo(member, Permissions.CHANGELOG_PUBLISHER_ADD)) {
            channel.createMessage(Embeds.error("Not enough permissions.")).block();
            return;
        }

        if (slug.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a project slug!")).block();
            return;
        }

        if (channelID.equals("empty")) {
            channel.createMessage(Embeds.error("You have not specified a channel id!")).block();
            return;
        }

        var project = ModrinthAPI.getProject(slug);

        if(project.isEmpty()) {
            channel.createMessage(Embeds.error("Project `" + slug + "` is not found.")).block();
            return;
        }

        for (var publisher : serverData.publishers) {
            if(publisher.channel.equals(channelID) && publisher.project.equals(project.get().id)) {
                channel.createMessage(Embeds.error("This channel already has a publisher of the `" + slug + "` project.")).block();
                return;
            }
        }

        ChangelogPublisher publisher = new ChangelogPublisher(project.get().id, channelID, serverData.serverId);

        serverData.publishers.add(publisher);
        serverData.saveData();

        String text = "Changelog publisher for the project `" + slug + "` got successfully created!";
        channel.createMessage(Embeds.success("Creating Changelog Publisher", text)).block();
    }
}
