package ru.pinkgoosik.kitsun.command.moderation;

import discord4j.core.object.entity.Member;
import discord4j.rest.entity.RestChannel;
import ru.pinkgoosik.kitsun.api.modrinth.ModrinthAPI;
import ru.pinkgoosik.kitsun.api.modrinth.entity.ModrinthProject;
import ru.pinkgoosik.kitsun.command.Command;
import ru.pinkgoosik.kitsun.command.CommandUseContext;
import ru.pinkgoosik.kitsun.instance.config.Config;
import ru.pinkgoosik.kitsun.instance.config.entity.ChangelogPublisherConfig;
import ru.pinkgoosik.kitsun.permission.AccessManager;
import ru.pinkgoosik.kitsun.permission.Permissions;
import ru.pinkgoosik.kitsun.util.Embeds;

import java.util.Optional;

public class ChangelogPublisherAddCommand extends Command {

    @Override
    public String getName() {
        return "changelog publisher add";
    }

    @Override
    public String getDescription() {
        return "Creates changelog publisher of the certain Modrinth Project.";
    }

    @Override
    public String appendName(Config config) {
        return super.appendName(config) + " <slug> <channel id>";
    }

    @Override
    public void respond(CommandUseContext context) {
        Member member = context.getMember();
        RestChannel channel = context.getChannel();
        String slug = context.getFirstArg();
        String channelID = context.getSecondArg();
        AccessManager accessManager = context.getServerData().accessManager;
        Config config = context.getServerData().config;

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

        for (var publisher : config.general.changelogPublishers) {
            if(publisher.channel.equals(channelID) && publisher.mod.equals(slug)) {
                channel.createMessage(Embeds.error("This channel already has a publisher of the `" + slug + "` project.")).block();
                return;
            }
        }

        Optional<ModrinthProject> project = ModrinthAPI.getProject(slug);

        if(project.isPresent()) {
            ChangelogPublisherConfig publisherConfig = new ChangelogPublisherConfig(slug, channelID);

            config.general.changelogPublishers.add(publisherConfig);
            config.saveConfig();

            String text = "Changelog publisher for the project `" + slug + "` got successfully created!";
            channel.createMessage(Embeds.success("Creating Changelog Publisher", text)).block();
        }else {
            channel.createMessage(Embeds.error("Project `" + slug + "` is not found.")).block();
        }
    }
}
